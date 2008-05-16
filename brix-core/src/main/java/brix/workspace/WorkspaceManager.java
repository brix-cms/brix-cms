package brix.workspace;

import java.util.List;
import java.util.Map;

public interface WorkspaceManager
{
    public List<Workspace> getWorkspaces();
    
    public List<Workspace> getWorkspacesFiltered(String workspaceName, Map<String, String> workspaceAttributes);    
    
    public Workspace createWorkspace();    
    
    public Workspace getWorkspace(String workspaceId);
    
    public void deleteWorkspace(String workspaceId);       
}
