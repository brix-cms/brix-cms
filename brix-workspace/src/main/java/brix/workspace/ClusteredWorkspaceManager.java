package brix.workspace;

/**
 * Workspace manager that can be running in clustered JCR environment. 
 * 
 * @author Matej Knopp
 */
public interface ClusteredWorkspaceManager extends WorkspaceManager
{

	/**
	 * Notification that a workspace has been created externally (e.g. on different node).
	 * 
	 * @param workspaceId
	 */
	public void workspaceCreated(String workspaceId);
	
}
