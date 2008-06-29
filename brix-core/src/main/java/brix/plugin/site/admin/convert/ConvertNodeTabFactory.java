package brix.plugin.site.admin.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.SiteNodeAction;
import brix.web.tab.CachingAbstractTab;

public class ConvertNodeTabFactory implements ManageNodeTabFactory
{

	public List<ITab> getManageNodeTabs(IModel<BrixNode> nodeModel)
	{
		List<ITab> result = new ArrayList<ITab>();

		if (hasEditPermission(nodeModel) && hasConverterForNode(nodeModel))
		{
			result.add(newTab(nodeModel));
		}

		return result;
	}

	private static ITab newTab(final IModel<BrixNode> nodeModel)
	{
		return new CachingAbstractTab(new Model<String>("Convert"))
		{
			@Override
			public Panel newPanel(String panelId)
			{
				return new ConvertTab(panelId, nodeModel);
			}
		};
	};

	private static boolean hasConverterForNode(IModel<BrixNode> nodeModel)
	{
		Collection<SiteNodePlugin> plugins = SitePlugin.get().getNodePlugins();
		BrixNode node = nodeModel.getObject();
		for (SiteNodePlugin plugin : plugins)
		{
			if (plugin.getConverterForNode(node) != null)
			{
				return true;
			}
		}
		return false;
	};

	private static boolean hasEditPermission(IModel<BrixNode> nodeModel)
	{
		Action action = new SiteNodeAction(Action.Context.ADMINISTRATION, SiteNodeAction.Type.NODE_EDIT, nodeModel
				.getObject());
		return nodeModel.getObject().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
	}

	public int getPriority()
	{
		return -1;
	}

}
