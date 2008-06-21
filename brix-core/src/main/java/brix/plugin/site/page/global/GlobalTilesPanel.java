package brix.plugin.site.page.global;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.admin.TilesPanel;
import brix.workspace.Workspace;

public class GlobalTilesPanel extends AbstractGlobalPanel
{

	public GlobalTilesPanel(String id, IModel<Workspace> workspaceModel)
	{
		super(id, workspaceModel);
	}

	@Override
	protected Panel<?> newManagePanel(String id, IModel<BrixNode> containerNodeModel)
	{
		return new TilesPanel(id, containerNodeModel);
	}

}
