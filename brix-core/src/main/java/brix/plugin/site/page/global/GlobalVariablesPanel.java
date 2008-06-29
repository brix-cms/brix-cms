package brix.plugin.site.page.global;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.admin.VariablesPanel;
import brix.workspace.Workspace;

public class GlobalVariablesPanel extends AbstractGlobalPanel
{
	public GlobalVariablesPanel(String id, IModel<Workspace> workspaceModel)
	{
		super(id, workspaceModel);
	}

	@Override
	protected Panel newManagePanel(String id, IModel<BrixNode> containerNodeModel)
	{
		return new VariablesPanel(id, containerNodeModel);
	}
}
