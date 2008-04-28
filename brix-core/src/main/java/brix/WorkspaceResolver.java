package brix;

public interface WorkspaceResolver
{
    public String getWorkspaceName(String prefix, String id, String state);
    
    public String getWorkspacePrefix(String workspaceName);
    
    public String getWorkspaceId(String workspaceName);
    
    public String getWorkspaceState(String workspaceName);
    
    public String getUserVisibleWorkspaceName(String workspaceId);
    
    public String getWorkspaceIdFromVisibleName(String visibleWorkspaceName);
    
    public boolean isValidWorkspaceName(String workspaceName);
}
