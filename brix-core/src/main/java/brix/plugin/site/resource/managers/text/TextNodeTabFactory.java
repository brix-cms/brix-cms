package brix.plugin.site.resource.managers.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.ResourceNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.auth.SiteNodeAction;
import brix.web.tab.CachingAbstractTab;

public class TextNodeTabFactory implements ManageNodeTabFactory
{
	public List<ITab> getManageNodeTabs(IModel<BrixNode> nodeModel)
	{
		List<ITab> result = new ArrayList<ITab>();

		BrixNode node = nodeModel.getObject();
		if (node instanceof ResourceNode && hasViewPermission(nodeModel)) 
		{
			String mime = ((BrixFileNode) node).getMimeType();
			if (canHandleMimeType(mime))
			{
				result.add(getViewTab(nodeModel));
			}
		}

		return result;
	}

	private static boolean canHandleMimeType(String mimeType)
	{
		return mimeType.startsWith("text/") || mimeType.equals("application/xml");
	}

	private static ITab getViewTab(final IModel<BrixNode> nodeModel)
	{
		return new CachingAbstractTab(new Model<String>("View"))
		{
			@Override
			public Panel<?> newPanel(String panelId)
			{
				return new ViewTextPanel(panelId, nodeModel);
			}
		};
	}

	private static boolean hasViewPermission(IModel<BrixNode> model)
	{
		Action action = new SiteNodeAction(Action.Context.ADMINISTRATION, SiteNodeAction.Type.NODE_VIEW, model
				.getObject());
		return model.getObject().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
	}

	public int getPriority()
	{
		return 100;
	}
}
