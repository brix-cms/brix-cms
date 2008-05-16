package brix.workspace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspaceManagerImpl implements WorkspaceManager
{

    public Workspace createWorkspace()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void deleteWorkspace(Workspace workspace)
    {
        // TODO Auto-generated method stub

    }

    public Workspace getWorkspace(String id)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Workspace> getWorkspaces()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Workspace> getWorkspacesFiltered(String workspaceName,
            Map<String, String> workspaceAttributes)
    {
        // TODO Auto-generated method stub
        return null;
    }

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
    
    private Map<AttributeKeyAndValue, String> attributeToWorkspaceListMap = new HashMap<AttributeKeyAndValue, String>();

    public void deleteWorkspace(String workspaceId)
    {
        // TODO Auto-generated method stub
        
    }

    public List<Workspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
