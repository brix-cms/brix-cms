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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Simple workspace manager. This class will not work properly in clustered JCR environment.
 *
 * @author Matej Knopp
 */
public abstract class AbstractSimpleWorkspaceManager extends AbstractWorkspaceManager {
    private static final Collection<String> NODES_TO_LEAVE_WHEN_CLEANING = Arrays
            .asList(NODE_NAME, "jcr:system", "rep:policy");

    private Set<String> availableWorkspaceNames;

    private List<String> deletedWorkspaceNames;



    public synchronized List<Workspace> getWorkspaces() {
        List<Workspace> result = new ArrayList<Workspace>(availableWorkspaceNames.size());

        for (String s : availableWorkspaceNames) {
            result.add(new WorkspaceImpl(s));
        }

        return result;
    }

    public Workspace createWorkspace() {
        Session session = null;
        try {
            synchronized (this) {
                if (deletedWorkspaceNames.size() > 0) {
                    String id = deletedWorkspaceNames.get(deletedWorkspaceNames.size() - 1);
                    deletedWorkspaceNames.remove(id);
                    availableWorkspaceNames.add(id);
                    session = createSession(id);
                    Node node = (Node) session.getItem(NODE_PATH);
                    node.setProperty(DELETED_PROPERTY, (String) null);
                    node.getSession().save();
                    closeSession(session, true);
                    session = null;
                    return new WorkspaceImpl(id);
                }
            }

            String id = getWorkspaceId(UUID.randomUUID());
            createWorkspace(id);
            synchronized (this) {
                session = createSession(id);
                Node node = session.getRootNode().addNode(NODE_NAME, "nt:unstructured");
                node.addMixin("mix:lockable");
                node.addNode(PROPERTIES_NODE, "nt:unstructured");
                closeSession(session, true);
                session = null;
                availableWorkspaceNames.add(id);
            }

            return new WorkspaceImpl(id);
        } catch (RepositoryException e) {
            closeSession(session, false);
            throw new JcrException(e);
        }
    }

    public synchronized boolean workspaceExists(String workspaceId) {
        return availableWorkspaceNames.contains(workspaceId);
    }

    abstract protected void createWorkspace(String workspaceName);

    protected void delete(String workspaceId) throws RepositoryException {
        synchronized (this) {
            if (!availableWorkspaceNames.contains(workspaceId)) {
                throw new IllegalStateException("Workspace " + workspaceId +
                        " either does not exist or was already deleted.");
            }
        }
        Session session = createSession(workspaceId);
        boolean saveSession = true;
        try {
            cleanWorkspace(session);
            session.save();

            Node node = (Node) session.getItem(NODE_PATH);
            if (node.hasNode(PROPERTIES_NODE)) {
                node.getNode(PROPERTIES_NODE).remove();
            }
            node.setProperty(DELETED_PROPERTY, true);
            node.getSession().save();

            synchronized (this) {
                availableWorkspaceNames.remove(workspaceId);
                deletedWorkspaceNames.add(workspaceId);
                removeCachedWorkspaceAttributes(workspaceId);
            }
        } catch (RepositoryException e) {
            saveSession = false;
            throw e;
        } finally {
            closeSession(session, saveSession);
        }
    }

    private void cleanWorkspace(Session session) throws RepositoryException {
        Node root = session.getRootNode();
        NodeIterator iterator = root.getNodes();
        while (iterator.hasNext()) {
            Node node = iterator.nextNode();
            if (!NODES_TO_LEAVE_WHEN_CLEANING.contains(node.getName())) {
                node.remove();
            }
        }
    }

    @Override
    protected synchronized String getAttribute(String workspaceId, String key) {
        if (!availableWorkspaceNames.contains(workspaceId)) {
            throw new IllegalStateException("Trying to get attribute of workspace " + workspaceId +
                    " that doesn't exist or was removed.");
        }
        return getCachedAttribute(workspaceId, key);
    }

    @Override
    protected Iterator<String> getAttributeKeys(String workspaceId) {
        if (!availableWorkspaceNames.contains(workspaceId)) {
            throw new IllegalStateException("Trying to get attribute keys of workspace " +
                    workspaceId + " that doesn't exist or was removed.");
        }
        return getCachedAttributeKeys(workspaceId);
    }

    public AbstractSimpleWorkspaceManager initialize() {
        super.initialize();

        Session session = null;

        try {
            availableWorkspaceNames = new HashSet<String>();
            deletedWorkspaceNames = new ArrayList<String>();

            List<String> accessibleWorkspaces = getAccessibleWorkspaceNames();
            for (String workspace : accessibleWorkspaces) {
                if (isBrixWorkspace(workspace)) {
                    session = createSession(workspace);
                    if (session.itemExists(NODE_PATH)) {
                        Node node = (Node) session.getItem(NODE_PATH);

                        if (node.hasProperty(DELETED_PROPERTY) &&
                                node.getProperty(DELETED_PROPERTY).getBoolean() == true) {
                            deletedWorkspaceNames.add(workspace);
                        } else {
                            availableWorkspaceNames.add(workspace);
                            if (node.hasNode(PROPERTIES_NODE)) {
                                Node properties = node.getNode(PROPERTIES_NODE);
                                PropertyIterator iterator = properties.getProperties();
                                while (iterator.hasNext()) {
                                    Property property = iterator.nextProperty();
                                    setCachedAttribute(workspace, property.getName(), property
                                            .getValue().getString());
                                }
                            }
                        }
                    }
                    closeSession(session, true);
                    session = null;
                }
            }
            return this;
        } catch (RepositoryException e) {
            closeSession(session, false);
            throw new JcrException(e);
        }
    }

    abstract protected List<String> getAccessibleWorkspaceNames();

    abstract protected Session createSession(String workspaceName);

    /**
     * Closes a session
     *
     * @param session     session to close
     * @param saveSession whether or not session.save() should be called before the session is closed
     */
    protected final void closeSession(Session session, boolean saveSession) {
        if (session != null && session.isLive()) {
            try {
                if (saveSession) {
                    try {
                        session.save();
                    } catch (RepositoryException e) {
                        throw new JcrException(e);
                    }
                }
            } finally {
                session.logout();
            }
        }
    }

    protected synchronized void setAttribute(String workspaceId, String key, String value) {
        Session session = null;
        boolean saveSession = true;
        if (!availableWorkspaceNames.contains(workspaceId)) {
            throw new IllegalStateException("Can not set attribute '" + key +
                    "' on deleted or non-existing workspace '" + workspaceId + "'.");
        }
        setCachedAttribute(workspaceId, key, value);
        try {
            session = createSession(workspaceId);
            Node node = (Node) session.getItem(NODE_PATH);
            Node properties;
            if (!node.hasNode(PROPERTIES_NODE)) {
                properties = node.addNode(PROPERTIES_NODE, "nt:unstructured");
            } else {
                properties = node.getNode(PROPERTIES_NODE);
            }

            properties.setProperty(key, value);

            node.getSession().save();
        } catch (RepositoryException e) {
            saveSession = false;
            throw new JcrException(e);
        } finally {
            closeSession(session, saveSession);
        }
    }
}
