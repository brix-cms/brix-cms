package brix.plugin.site.folder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.auth.SiteNodeAction;

public class ManageFolderNodeTabFactory implements ManageNodeTabFactory
{

	public List<ITab> getManageNodeTabs(IModel<BrixNode> nodeModel)
	{
		if (nodeModel.getObject().isFolder())
		{
			return getTabs(nodeModel);
		}
		else
		{
			return null;
		}
	}

	public int getPriority()
	{
		return 0;
	}

	public static List<ITab> getTabs(final IModel<BrixNode> folderModel)
	{
		List<ITab> tabs = new ArrayList<ITab>(2);
		tabs.add(new AbstractTab(new Model<String>("Listing"))
		{

			@Override
			public Panel<?> getPanel(String panelId)
			{
				return new ListFolderNodesTab(panelId, folderModel);
			}

			@Override
			public boolean isVisible()
			{
				Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
						SiteNodeAction.Type.NODE_VIEW_CHILDREN, folderModel.getObject());
				return folderModel.getObject().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
			}

		});
		tabs.add(new AbstractTab(new Model<String>("Properties"))
		{

			@Override
			public Panel<?> getPanel(String panelId)
			{
				return new PropertiesTab(panelId, folderModel);
			}

			@Override
			public boolean isVisible()
			{
				Action action = new SiteNodeAction(Action.Context.ADMINISTRATION, SiteNodeAction.Type.NODE_EDIT,
						folderModel.getObject());
				return folderModel.getObject().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
			}

		});
		return tabs;
	}

}
