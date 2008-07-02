package brix.workspace;

import java.util.Iterator;

/**
 * Represents a single workspace. Allows to set and query workspace attributes.
 * To obtain a workspace object use {@link WorkspaceManager}.
 * 
 * @author Matej Knopp
 */
public interface Workspace {

	/**
	 * Returns workspace id. Workspace id is a JCR workspace name.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Sets the attribute with specified key to specified value. To remove
	 * attribute use <code>null</code> value.
	 * 
	 * @param attributeKey
	 * @param attributeValue
	 */
	public void setAttribute(String attributeKey, String attributeValue);

	/**
	 * Returns attribute with the specified key.
	 * 
	 * @param attributeKey
	 * @return attribute value or <code>null</code> if such attribute is not
	 *         present.
	 */
	public String getAttribute(String attributeKey);

	/**
	 * Returns all available attribute keys in this workspace.
	 * 
	 * @return
	 */
	public Iterator<String> getAttributeKeys();

	/**
	 * Deletes the workspace.
	 */
	public void delete();
}
