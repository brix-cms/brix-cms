package brix.plugin.site.page.global;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.generic.BrixGenericPanel;
import brix.workspace.Workspace;

public abstract class AbstractGlobalPanel extends BrixGenericPanel<BrixNode> 
{
	IModel<Workspace> workspaceModel;

	private static BrixNode getContainerNode(Workspace workspace)
	{
		JcrSession session = Brix.get().getCurrentSession(workspace.getId());
		return SitePlugin.get().getGlobalContainer(session);
	}
	
	public AbstractGlobalPanel(String id, IModel<Workspace> workspaceModel)
	{
		super(id, new BrixNodeModel(getContainerNode(workspaceModel.getObject())));
		
		this.workspaceModel = workspaceModel;
	}
	
	protected abstract Panel newManagePanel(String id, IModel<BrixNode> containerNodeModel);
	
	private static final String PANEL_ID = "managePanel";

	@Override
	protected void onBeforeRender()
	{
		boolean isInvalidWorkspace = !getModelObject().getSession().getWorkspace().getName().equals(workspaceModel.getObject().getId()); 		
		if (!hasBeenRendered())
		{
			add(newManagePanel(PANEL_ID, getModel()));
		}
		else if (isInvalidWorkspace)
		{
			setModelObject(getContainerNode(workspaceModel.getObject()));
			get(PANEL_ID).replaceWith(newManagePanel(PANEL_ID, getModel()));
		}
		super.onBeforeRender();
	}
}
