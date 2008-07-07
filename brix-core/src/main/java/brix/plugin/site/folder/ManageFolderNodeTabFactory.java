package brix.plugin.site.folder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;
import brix.web.tab.CachingAbstractTab;
import brix.web.tab.IBrixTab;

public class ManageFolderNodeTabFactory implements ManageNodeTabFactory
{

	public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel)
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


	public static List<IBrixTab> getTabs(final IModel<BrixNode> folderModel)
	{
		List<IBrixTab> tabs = new ArrayList<IBrixTab>(2);
		tabs.add(new CachingAbstractTab(new Model<String>("Listing"), 100)
		{

			@Override
			public Panel newPanel(String panelId)
			{
				return new ListFolderNodesTab(panelId, folderModel);
			}

			@Override
			public boolean isVisible()
			{
				return SitePlugin.get().canViewNodeChildren(folderModel.getObject(), Context.ADMINISTRATION);
			}

		});
		tabs.add(new CachingAbstractTab(new Model<String>("Properties"))
		{

			@Override
			public Panel newPanel(String panelId)
			{
				return new PropertiesTab(panelId, folderModel);
			}

			@Override
			public boolean isVisible()
			{
				return SitePlugin.get().canEditNode(folderModel.getObject(), Context.ADMINISTRATION);
			}

		});
		return tabs;
	}

}
