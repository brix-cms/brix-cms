package brix.workspace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public abstract class AbstractWorkspaceManager implements WorkspaceManager
{
    private class AttributeKeyAndValue
    {
        private final String key;
        private final String value;

        public AttributeKeyAndValue(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj instanceof AttributeKeyAndValue == false)
                return false;
            AttributeKeyAndValue that = (AttributeKeyAndValue)obj;
            return equals(key, that.key) && equals(value, that.value);
        }

        @Override
        public int hashCode()
        {
            return hashCode(key) + 31 * hashCode(value);
        }

        private boolean equals(Object o1, Object o2)
        {
            return o1 == o2 || (o1 != null && o1.equals(o2));
        }

        private int hashCode(Object o)
        {
            return o != null ? o.hashCode() : 0;
        }
    }

    private Map<AttributeKeyAndValue, Set<String>> attributeToWorkspaceListMap = new HashMap<AttributeKeyAndValue, Set<String>>();

    private Map<String, Map<String, String>> workspaceToWorkspaceAttributesMap = new HashMap<String, Map<String, String>>();

    private String getCachedAttribute(String workspaceId, String key)
    {
        Map<String, String> attributes = workspaceToWorkspaceAttributesMap.get(workspaceId);
        if (attributes != null)
        {
            return attributes.get(key);
        }
        else
        {
            return null;
        }
    }

    private void removeCachedAttribute(String workspaceId, String key)
    {
        String value = getCachedAttribute(workspaceId, key);
        if (value != null)
        {
            Map<String, String> attributes = workspaceToWorkspaceAttributesMap.get(workspaceId);
            attributes.remove(key);
            AttributeKeyAndValue keyAndValue = new AttributeKeyAndValue(key, value);
            Set<String> workspaces = attributeToWorkspaceListMap.get(keyAndValue);
            if (workspaces != null)
            {
                workspaces.remove(workspaceId);
            }
        }
    }

    private void setCachedAttribute(String workspaceId, String key, String value)
    {
        removeCachedAttribute(workspaceId, key);
        if (value != null)
        {
            Map<String, String> workspaceAttributes = workspaceToWorkspaceAttributesMap
                .get(workspaceId);
            if (workspaceAttributes == null)
            {
                workspaceAttributes = new HashMap<String, String>();
                workspaceToWorkspaceAttributesMap.put(workspaceId, workspaceAttributes);
            }
            workspaceAttributes.put(key, value);

            AttributeKeyAndValue keyAndValue = new AttributeKeyAndValue(key, value);
            Set<String> workspaces = attributeToWorkspaceListMap.get(keyAndValue);
            if (workspaces == null)
            {
                workspaces = new HashSet<String>();
                attributeToWorkspaceListMap.put(keyAndValue, workspaces);
            }
            if (!workspaces.contains(workspaceId))
            {
                workspaces.add(workspaceId);
            }
        }
    }

    private Set<String> availableWorkspaceNames;

    private List<String> deletedWorkspaceNames;

    private static final String NODE_NAME = "brix:workspace";

    private static final String NODE_PATH = "/" + NODE_NAME;

    private static final String PROPERTIES_NODE = "properties";

    private static final String DELETED_PROPERTY = "deleted";

    public AbstractWorkspaceManager initialize()
    {
        Session session = null;

        try
        {
            availableWorkspaceNames = new HashSet<String>();

            deletedWorkspaceNames = new ArrayList<String>();

            List<String> accessibleWorkspaces = getAccessibleWorkspaceNames();
            for (String workspace : accessibleWorkspaces)
            {
                if (isBrixWorkspace(workspace))
                {
                    session = createSession(workspace);
                    if (session.itemExists(NODE_PATH))
                    {
                        Node node = (Node)session.getItem(NODE_PATH);

                        if (node.hasProperty(DELETED_PROPERTY) &&
                            node.getProperty(DELETED_PROPERTY).getBoolean() == true)
                        {
                            deletedWorkspaceNames.add(workspace);
                        }
                        else
                        {
                            availableWorkspaceNames.add(workspace);
                            if (node.hasNode(PROPERTIES_NODE))
                            {
                                Node properties = node.getNode(PROPERTIES_NODE);
                                PropertyIterator iterator = properties.getProperties();
                                while (iterator.hasNext())
                                {
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
        }
        catch (RepositoryException e)
        {
            closeSession(session, false);
            throw new JcrException(e);
        }
    }

    protected synchronized void setAttribute(String workspaceId, String key, String value)
    {
        Session session = null;
        boolean saveSession = true;
        if (!availableWorkspaceNames.contains(workspaceId))
        {
            throw new IllegalStateException("Can not set attribute '" + key +
                "' on deleted or non-existing workspace '" + workspaceId + "'.");
        }
        setCachedAttribute(workspaceId, key, value);
        try
        {
            session = createSession(workspaceId);
            Node node = (Node)session.getItem(NODE_PATH);
            Node properties;
            if (!node.hasNode(PROPERTIES_NODE))
            {
                properties = node.addNode(PROPERTIES_NODE, "nt:unstructured");
            }
            else
            {
                properties = node.getNode(PROPERTIES_NODE);
            }

            properties.setProperty(key, value);

            node.save();
        }
        catch (RepositoryException e)
        {
            saveSession = false;
            throw new JcrException(e);
        }
        finally
        {
            closeSession(session, saveSession);
        }
    }

    protected void closeSession(Session session, boolean saveSession)
    {
        if (session != null)
        {
            try
            {
                if (saveSession)
                {
                    try
                    {
                        session.save();
                    }
                    catch (RepositoryException e)
                    {
                        throw new JcrException(e);
                    }
                }
            }
            finally
            {
                session.logout();
            }
        }

    }

    protected synchronized Iterator<String> getAtributeKeys(String workspaceId)
    {
        Map<String, String> attributes = workspaceToWorkspaceAttributesMap.get(workspaceId);
        if (attributes == null || attributes.isEmpty())
        {
            List<String> empty = Collections.emptyList();
            return empty.iterator();
        }
        else
        {
            return new ArrayList<String>(attributes.keySet()).iterator();
        }
    }

    protected synchronized String getAttribute(String workspaceId, String key)
    {
        if (!availableWorkspaceNames.contains(workspaceId))
        {
            throw new IllegalStateException("Trying to get attribute of workspace " + workspaceId +
                " that doesn't exist or was removed.");
        }
        return getCachedAttribute(workspaceId, key);
    }

    private class WorkspaceImpl implements Workspace
    {
        private final String id;

        public WorkspaceImpl(String id)
        {
            this.id = id;
        }

        public String getId()
        {
            return id;
        }

        public String getAttribute(String attributeKey)
        {
            return AbstractWorkspaceManager.this.getAttribute(getId(), attributeKey);
        }

        public Iterator<String> getAttributeKeys()
        {
            return AbstractWorkspaceManager.this.getAtributeKeys(getId());
        }

        public void setAttribute(String attributeKey, String attributeValue)
        {
            AbstractWorkspaceManager.this.setAttribute(getId(), attributeKey, attributeValue);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj instanceof WorkspaceImpl == false)
            {
                return false;
            }
            WorkspaceImpl that = (WorkspaceImpl)obj;

            if (id == that.id)
            {
                return true;
            }
            else if (id == null || that.id == null)
            {
                return false;
            }
            return id.equals(that.id);
        }

        @Override
        public int hashCode()
        {
            return id != null ? id.hashCode() : 0;
        }

        public void delete()
        {
            try
            {
                AbstractWorkspaceManager.this.delete(getId());
            }
            catch (RepositoryException e)
            {
                throw new JcrException(e);
            }
        }
    };

    private final String WORKSPACE_PREFIX = "brix-workspace-";

    private String getWorkspaceId(String uuid)
    {
        return WORKSPACE_PREFIX + uuid;
    }

    private boolean isBrixWorkspace(String id)
    {
        // we could check is the prefix is followed by real uuid here, but
        // that's probably an
        // overkill.
        return id.startsWith(WORKSPACE_PREFIX);
    }

    public Workspace createWorkspace()
    {
        Session session = null;
        try
        {
            synchronized (this)
            {
                if (deletedWorkspaceNames.size() > 0)
                {
                    String id = deletedWorkspaceNames.get(deletedWorkspaceNames.size() - 1);
                    deletedWorkspaceNames.remove(id);
                    availableWorkspaceNames.add(id);
                    session = createSession(id);
                    Node node = (Node)session.getItem(NODE_PATH);
                    node.setProperty(DELETED_PROPERTY, (String)null);
                    node.save();
                    closeSession(session, true);
                    session = null;
                    return new WorkspaceImpl(id);
                }
            }

            String id = getWorkspaceId(UUID.randomUUID().toString());
            createWorkspace(id);
            synchronized (this)
            {
                session = createSession(id);
                Node node = session.getRootNode().addNode(NODE_NAME, "nt:unstructured");
                node.addNode(PROPERTIES_NODE, "nt:unstructured");
                closeSession(session, true);
                session = null;
                availableWorkspaceNames.add(id);
            }

            return new WorkspaceImpl(id);

        }
        catch (RepositoryException e)
        {
            closeSession(session, false);
            throw new JcrException(e);
        }
    }

    private void cleanWorkspace(Session session) throws RepositoryException
    {
        Node root = session.getRootNode();
        NodeIterator iterator = root.getNodes();
        while (iterator.hasNext())
        {
            Node node = iterator.nextNode();
            if (!node.getName().equals(NODE_NAME) && !node.getName().equals("jcr:system"))
            {
                node.remove();
            }
        }
    }

    private void delete(String workspaceId) throws RepositoryException
    {
        synchronized (this)
        {
            if (!availableWorkspaceNames.contains(workspaceId))
            {
                throw new IllegalStateException("Workspace " + workspaceId +
                    " either does not exist or was already deleted.");
            }
        }
        Session session = createSession(workspaceId);
        boolean saveSession = true;
        try
        {
            cleanWorkspace(session);
            session.save();

            Node node = (Node)session.getItem(NODE_PATH);
            if (node.hasNode(PROPERTIES_NODE))
            {
                node.getNode(PROPERTIES_NODE).remove();
            }
            node.setProperty(DELETED_PROPERTY, true);
            node.save();

            synchronized (this)
            {
                availableWorkspaceNames.remove(workspaceId);
                deletedWorkspaceNames.add(workspaceId);

                workspaceToWorkspaceAttributesMap.remove(workspaceId);
                for (Iterator<Entry<AttributeKeyAndValue, Set<String>>> i = attributeToWorkspaceListMap
                    .entrySet().iterator(); i.hasNext();)
                {
                    Entry<AttributeKeyAndValue, Set<String>> e = i.next();
                    if (e.getValue().contains(workspaceId))
                    {
                        e.getValue().remove(workspaceId);
                        if (e.getValue().isEmpty())
                        {
                            i.remove();
                        }
                    }
                }
            }
        }
        catch (RepositoryException e)
        {
            saveSession = false;
            throw e;
        }
        finally
        {
            closeSession(session, saveSession);
        }
    }

    public Workspace getWorkspace(String workspaceId)
    {
        return new WorkspaceImpl(workspaceId);
    }

    public synchronized List<Workspace> getWorkspaces()
    {
        List<Workspace> result = new ArrayList<Workspace>(availableWorkspaceNames.size());

        for (String s : availableWorkspaceNames)
        {
            result.add(new WorkspaceImpl(s));
        }

        return result;
    }

    public synchronized List<Workspace> getWorkspacesFiltered(
            Map<String, String> workspaceAttributes)
    {
        if (workspaceAttributes.isEmpty())
        {
            return getWorkspaces();
        }
        else
        {
            List<Set<String>> workspaces = new ArrayList<Set<String>>();
            for (Entry<String, String> entry : workspaceAttributes.entrySet())
            {
                AttributeKeyAndValue keyAndValue = new AttributeKeyAndValue(entry.getKey(), entry
                    .getValue());
                Set<String> w = attributeToWorkspaceListMap.get(keyAndValue);
                if (w == null || w.isEmpty())
                {
                    return Collections.emptyList();
                }
                else
                {
                    workspaces.add(w);
                }
            }

            Set<String> intersection = intersect(workspaces);
            List<Workspace> result = new ArrayList<Workspace>(intersection.size());

            for (String s : intersection)
            {
                result.add(new WorkspaceImpl(s));
            }

            return result;
        }
    }

    private Set<String> intersect(List<Set<String>> sets)
    {
        if (sets.isEmpty())
        {
            return Collections.emptySet();
        }
        else if (sets.size() == 1)
        {
            return sets.get(0);
        }

        Collections.sort(sets, new Comparator<Set<String>>()
        {
            public int compare(Set<String> o1, Set<String> o2)
            {
                return o1.size() - o2.size();
            }
        });

        Set<String> current = sets.get(0);

        for (int i = 1; i < sets.size(); ++i)
        {
            current = intersect(current, sets.get(i));
        }

        return current;
    }

    private Set<String> intersect(Set<String> s1, Set<String> s2)
    {
        Set<String> result = new HashSet<String>(s1.size());
        for (String s : s1)
        {
            if (s2.contains(s))
            {
                result.add(s);
            }
        }
        return result;
    }

    public synchronized boolean workspaceExists(String id)
    {
        return availableWorkspaceNames.contains(id);
    }

    abstract protected List<String> getAccessibleWorkspaceNames();

    abstract protected Session createSession(String workspaceName);

    abstract protected void createWorkspace(String workspaceName);
}
