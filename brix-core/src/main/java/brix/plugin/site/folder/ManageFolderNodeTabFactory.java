package brix.plugin.site.folder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;

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
			public Panel getPanel(String panelId)
			{
				return new ListFolderNodesTab(panelId, folderModel);
			}

			@Override
			public boolean isVisible()
			{
				return SitePlugin.get().canViewNodeChildren(folderModel.getObject(), Context.ADMINISTRATION);
			}

		});
		tabs.add(new AbstractTab(new Model<String>("Properties"))
		{

			@Override
			public Panel getPanel(String panelId)
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
