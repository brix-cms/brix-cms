package brix.workspace;

import java.util.List;
import java.util.Map;

/**
 * Workspace manager is a think layer on top of JCR extending the JCR workspace
 * management functionality. It allows to delete workspaces, set workspace
 * attributes and filter workspaces according to the workspaces.
 * 
 * @author Matej Knopp
 */
public interface WorkspaceManager {

	/**
	 * Returns the list of all available workspaces.
	 * 
	 * @return
	 */
	public List<Workspace> getWorkspaces();

	/**
	 * Returns a filtered list of workspaces. Each workspace in the resulting
	 * list must have all specified workspace attributes set and the attributes
	 * values must be equal to the values in the
	 * <code>workspaceAttributes</code>map.
	 * 
	 * @param workspaceAttributes
	 * @return
	 */
	public List<Workspace> getWorkspacesFiltered(
			Map<String, String> workspaceAttributes);

	/**
	 * Creates a new workspace.
	 * 
	 * @return
	 */
	public Workspace createWorkspace();

	/**
	 * Returns the {@link Workspace} object associated with the workspace with
	 * given id. The workspace id actually a JCR workspace name.
	 * 
	 * @param workspaceId
	 * @return
	 */
	public Workspace getWorkspace(String workspaceId);

	/**
	 * Returns whether a workspace with given id exists.
	 *  
	 * @param workspaceId
	 * @return
	 */
	public boolean workspaceExists(String workspaceId);
}
