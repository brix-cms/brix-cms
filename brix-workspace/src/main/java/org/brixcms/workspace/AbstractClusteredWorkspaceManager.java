/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.workspace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractClusteredWorkspaceManager extends AbstractWorkspaceManager implements
        ClusteredWorkspaceManager {
    private static final Logger log = LoggerFactory.getLogger(AbstractClusteredWorkspaceManager.class);

    private Long lockTimeoutHint = 360L;
    private String ownerInfo = "Locked by AbstractClusteredWorkspaceManager";

    // property used to mark deleted workspace that couldn't have all nodes
    // deleted
    // such workspace can only be deleted manually when all nodes are down
    private final String PROPERTY_DO_NOT_USE = "doNotUse";
    private final Set<String> availableWorkspaceNames = new HashSet<String>();

    private final Set<String> deletedWorkspaceNames = new HashSet<String>();

    private final Map<String, Session> workspaceToSessionMap = new HashMap<String, Session>();

    public AbstractClusteredWorkspaceManager() {

    }


    public void workspaceCreated(String workspaceId) {
        // register the listener
        if (isBrixWorkspace(workspaceId)) {
            getSession(workspaceId);
        }
    }


    public synchronized List<Workspace> getWorkspaces() {
        List<Workspace> result = new ArrayList<Workspace>();
        for (String s : availableWorkspaceNames) {
            result.add(getWorkspace(s));
        }
        return result;
    }

    public synchronized Workspace createWorkspace() {
        try {
            // either try to restore deleted workspace
            for (String s : deletedWorkspaceNames) {
                if (restoreWorkspace(s)) {
                    return getWorkspace(s);
                }
            }

            // or create new one
            String id = getWorkspaceId(UUID.randomUUID());
            createWorkspace(id);
            Session session = getSession(id);
            Node node = session.getRootNode().addNode(NODE_NAME, "nt:unstructured");
            node.addMixin("mix:lockable");
            node.addNode(PROPERTIES_NODE, "nt:unstructured");
            availableWorkspaceNames.add(id);
            session.save();

            return getWorkspace(id);
        } catch (RepositoryException e) {
            throw new JcrException(e);
        }
    }

    public synchronized boolean workspaceExists(String workspaceId) {
        return availableWorkspaceNames.contains(workspaceId);
    }

    abstract protected void createWorkspace(String workspaceId);

    @Override
    protected void delete(String workspaceId) throws RepositoryException {
        synchronized (this) {
            if (!availableWorkspaceNames.contains(workspaceId)) {
                throw new IllegalStateException("Workspace " + workspaceId
                        + " either does not exist or was already deleted.");
            }
        }

        Session session = getSession(workspaceId);
        Node node = (Node) session.getItem(NODE_PATH);
        tryLockNode(node);
        try {
            synchronized (this) {
                availableWorkspaceNames.remove(workspaceId);
                removeCachedWorkspaceAttributes(workspaceId);

                node.setProperty(DELETED_PROPERTY, true);
                if (node.hasNode(PROPERTIES_NODE)) {
                    node.getNode(PROPERTIES_NODE).remove();
                }
                node.getSession().save();
            }

            try {
                cleanWorkspace(session);
                session.save();
            } catch (RepositoryException e) {
                // problem deleting nodes
                node.setProperty(PROPERTY_DO_NOT_USE, true);
                node.getSession().save();
                throw (e);
            }

            synchronized (this) {
                deletedWorkspaceNames.add(workspaceId);
            }
        } finally {
            node.getSession().getWorkspace().getLockManager().unlock(node.getPath());
        }
    }

    private synchronized void tryLockNode(Node node) throws RepositoryException {
        int sleep = 1000;
        for (int i = 0; i < 10; ++i) {
            if (!node.isLocked()) {
                try {
                    node.getSession().getWorkspace().getLockManager().lock(node.getPath(), false, true, lockTimeoutHint, ownerInfo);
                    return;
                } catch (LockException e) {
                }
            }
            try {
                log.info("Node already locked, waiting...");
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
            }
        }
        throw new RuntimeException("Couldn't lock " + node.getPath());
    }

    private void cleanWorkspace(Session session) throws RepositoryException {
        Node root = session.getRootNode();
        NodeIterator iterator = root.getNodes();
        while (iterator.hasNext()) {
            Node node = iterator.nextNode();
            if (!node.getName().equals(NODE_NAME) && !node.getName().equals("jcr:system")) {
                node.remove();
            }
        }
    }

    @Override
    protected synchronized String getAttribute(String workspaceId, String key) {
        if (!availableWorkspaceNames.contains(workspaceId)) {
            throw new IllegalStateException("Trying to get attribute of workspace " + workspaceId
                    + " that doesn't exist or was removed.");
        }
        return getCachedAttribute(workspaceId, key);
    }

    @Override
    protected Iterator<String> getAttributeKeys(String workspaceId) {
        if (!availableWorkspaceNames.contains(workspaceId)) {
            throw new IllegalStateException("Trying to get attribute keys of workspace " + workspaceId
                    + " that doesn't exist or was removed.");
        }
        return getCachedAttributeKeys(workspaceId);
    }

    public synchronized AbstractClusteredWorkspaceManager initialize() {
        try {
            List<String> accessibleWorkspaces = new ArrayList<String>(getAccessibleWorkspaceIds());

            // loop until all workspaces are processed or there were 20 attempts
            for (int i = 0; i < 20 && !accessibleWorkspaces.isEmpty(); ++i) {
                initialize(accessibleWorkspaces);

                if (!accessibleWorkspaces.isEmpty()) {
                    int wait = 1000;
                    log.info("Couldn't read all workspaces, some of them were locked, waiting " + wait
                            + " milliseconds.");
                    try {
                        Thread.sleep(wait);
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (!accessibleWorkspaces.isEmpty()) {
                log.info("Some workspaces couldn't be read during initialization (they were locked).");
            }

            return this;
        } catch (RepositoryException e) {
            throw new JcrException(e);
        }
    }

    abstract protected List<String> getAccessibleWorkspaceIds();

    /**
     * Iterates over the list of workspaces gathering information about each workspace. Every processed workspace is
     * removed from the list. Workspaces left in list could not have been locked properly.
     *
     * @param accessibleWorkspaces
     * @throws RepositoryException
     */
    private void initialize(List<String> accessibleWorkspaces) throws RepositoryException {
        for (Iterator<String> i = accessibleWorkspaces.iterator(); i.hasNext();) {
            String workspace = i.next();

            if (isBrixWorkspace(workspace)) {
                Session session = getSession(workspace);
                if (session.itemExists(NODE_PATH)) {
                    Node node = (Node) session.getItem(NODE_PATH);

                    // fix the node if it is not lockable
                    if (!node.isNodeType("mix:lockable")) {
                        node.addMixin("mix:lockable");
                        node.getSession().save();
                    }

                    // ignore workspaces in which the node is either locked or
                    // can not be locked
                    if (node.isLocked()) {
                        continue;
                    }

                    try {
                        node.getSession().getWorkspace().getLockManager().lock(node.getPath(), false, true, lockTimeoutHint, ownerInfo);
                    } catch (LockException e) {
                        continue;
                    }

                    try {
                        // determine whether the workspace is deleted or
                        // available
                        if (node.hasProperty(DELETED_PROPERTY)
                                && node.getProperty(DELETED_PROPERTY).getBoolean() == true) {
                            deletedWorkspaceNames.add(workspace);
                        } else {
                            // for available workspaces read the properties
                            availableWorkspaceNames.add(workspace);
                            if (node.hasNode(PROPERTIES_NODE)) {
                                Node properties = node.getNode(PROPERTIES_NODE);
                                PropertyIterator iterator = properties.getProperties();
                                while (iterator.hasNext()) {
                                    Property property = iterator.nextProperty();
                                    setCachedAttribute(workspace, property.getName(), property.getValue().getString());
                                }
                            }
                        }
                    } finally {
                        node.getSession().getWorkspace().getLockManager().unlock(node.getPath());
                    }
                }
            }
            i.remove();
        }
    }

    synchronized Session getSession(String workspaceId) {
        try {
            Session session = workspaceToSessionMap.get(workspaceId);
            if (session == null) {
                session = createSession(workspaceId);
                EventListener listener = new SessionEventListener(session);
                int events = Event.NODE_ADDED | Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
                session.getWorkspace().getObservationManager().addEventListener(listener, events, "/", true,
                        null, null, true);

                // we need to keep the sessions opened otherwise the listeners will be removed
                workspaceToSessionMap.put(workspaceId, session);
            }
            return session;
        } catch (RepositoryException e) {
            throw new JcrException(e);
        }
    }

    abstract protected Session createSession(String workspaceId);

    /**
     * Tries to restore the deleted workspace.
     *
     * @param workspaceId
     * @return <code>true</code> if the workspace was succesfully restored, <code>false</code> otherwise
     * @throws RepositoryException
     */
    private boolean restoreWorkspace(String workspaceId) throws RepositoryException {
        Session s = getSession(workspaceId);
        Node node = (Node) s.getItem(NODE_PATH);
        try {
            if (node.isLocked()) {
                return false;
            }
            node.getSession().getWorkspace().getLockManager().lock(node.getPath(), false, false, lockTimeoutHint, ownerInfo);
            try {
                // if the workspace is still deleted
                if (node.hasProperty(DELETED_PROPERTY)
                        && node.getProperty(DELETED_PROPERTY).getBoolean() == true
                        && (!node.hasProperty(PROPERTY_DO_NOT_USE) || node.getProperty(PROPERTY_DO_NOT_USE)
                        .getBoolean() == false)) {
                    node.setProperty(DELETED_PROPERTY, (String) null);
                    availableWorkspaceNames.add(workspaceId);
                    deletedWorkspaceNames.remove(workspaceId);

                    // clear properties if there are any
                    if (node.hasNode(PROPERTIES_NODE)) {
                        node.getNode(PROPERTIES_NODE).remove();
                    }

                    node.addNode(PROPERTIES_NODE, "nt:unstructured");

                    s.save();
                    return false;
                } else {
                    return false;
                }
            } finally {
                node.getSession().getWorkspace().getLockManager().unlock(node.getPath());
            }
        } catch (LockException e) {
            return false;
        }
    }

    @Override
    protected synchronized void setAttribute(String workspaceId, String attributeKey, String attributeValue)
            throws RepositoryException {
        if (!availableWorkspaceNames.contains(workspaceId)) {
            throw new IllegalStateException("Trying to set attribute of workspace " + workspaceId
                    + " that doesn't exist or was removed.");
        }

        Session session = getSession(workspaceId);
        Node node = (Node) session.getItem(NODE_PATH);
        Node properties;
        if (!node.hasNode(PROPERTIES_NODE)) {
            properties = node.addNode(PROPERTIES_NODE, "nt:unstructured");
        } else {
            properties = node.getNode(PROPERTIES_NODE);
        }

        properties.setProperty(attributeKey, attributeValue);

        node.getSession().save();

        setCachedAttribute(workspaceId, attributeKey, attributeValue);
    }

    private class SessionEventListener implements EventListener {
        private final Session session;

        public SessionEventListener(Session session) {
            this.session = session;
        }

        public void onEvent(EventIterator events) {
            synchronized (AbstractClusteredWorkspaceManager.this) {
                while (events.hasNext()) {
                    processEvent(events.nextEvent());
                }
            }
        }

        private void workspaceCreated() {
            String name = session.getWorkspace().getName();
            availableWorkspaceNames.add(name);
            deletedWorkspaceNames.remove(name);
        }

        private void workspaceRemoved() {
            String name = session.getWorkspace().getName();
            availableWorkspaceNames.remove(name);
            deletedWorkspaceNames.add(name);
            removeCachedWorkspaceAttributes(name);
        }

        private void attributeChanged(String key, String value) {
            String name = session.getWorkspace().getName();
            setCachedAttribute(name, key, value);
        }

        public void processEvent(Event event) {
            try {
                final String deletedPropertyPath = NODE_PATH + "/" + DELETED_PROPERTY;
                final String propertiesPath = NODE_PATH + "/" + PROPERTIES_NODE + "/";
                final String path = event.getPath();

                if (event.getType() == Event.PROPERTY_REMOVED) {
                    if (path.equals(deletedPropertyPath)) {
                        // deleted property of brix:workspace removed -> workspace was restored
                        workspaceCreated();
                    }
                    if (path.startsWith(propertiesPath)) {
                        // removed a property
                        attributeChanged(path.substring(propertiesPath.length()), null);
                    }
                } else if (event.getType() == Event.PROPERTY_ADDED || event.getType() == Event.PROPERTY_CHANGED) {
                    Property property = (Property) session.getItem(path);

                    // deleted property of brix:workspace
                    if (path.equals(deletedPropertyPath)) {
                        if (property.getValue().getBoolean() == true) {
                            workspaceRemoved();
                        } else {
                            workspaceCreated();
                        }
                    } else if (path.startsWith(propertiesPath)) {
                        attributeChanged(property.getName(), property.getValue().getString());
                    }
                } else if (event.getType() == Event.NODE_ADDED) {
                    // new workspace - created brix:workspace node
                    if (path.equals(NODE_PATH)) {
                        workspaceCreated();
                    }
                }
            } catch (RepositoryException e) {
                log.warn("Error processing event ", e);
            }
        }
    }
}
