/**
 * 
 */
package brix.plugin.menu.tile;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.menu.MenuPlugin;

/**
 * A model that creates a list of all available menu nodes
 * 
 * @author igor.vaynberg
 * 
 */
public final class MenuNodesListModel extends LoadableDetachableModel<List<BrixNode>>
{
	private final IModel<BrixNode> workspaceNodeModel;

	MenuNodesListModel(IModel<BrixNode> workspaceNodeModel)
	{
		this.workspaceNodeModel = workspaceNodeModel;
	}

	@Override
	protected List<BrixNode> load()
	{
		return MenuPlugin.get().getMenuNodes(
				workspaceNodeModel.getObject().getSession().getWorkspace().getName());
	}

	@Override
	public void detach()
	{
		super.detach();
		workspaceNodeModel.detach();
	}
}