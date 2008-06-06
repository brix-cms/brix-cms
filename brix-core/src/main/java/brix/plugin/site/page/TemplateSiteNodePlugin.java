package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.page.admin.CreateTilePagePanel;
import brix.web.admin.navigation.NavigationAwarePanel;

public class TemplateSiteNodePlugin extends AbstractSitePagePlugin
{

	public static final String TYPE = Brix.NS_PREFIX + "tileTemplate";

	public TemplateSiteNodePlugin(Brix brix)
	{
		super(brix);
	}

	@Override
	public NodeConverter getConverterForNode(BrixNode node)
	{
		if (PageSiteNodePlugin.TYPE.equals(((BrixNode) node).getNodeType()))
			return new FromPageConverter(getNodeType());
		else
			return super.getConverterForNode(node);
	}

	private static class FromPageConverter extends SetTypeConverter
	{
		public FromPageConverter(String type)
		{
			super(type);
		}
	};

	@Override
	public NavigationAwarePanel newCreateNodePanel(String id, IModel<BrixNode> parentNode)
	{
		return new CreateTilePagePanel(id, parentNode, getNodeType());
	}

	@Override
	public String getNodeType()
	{
		return TYPE;
	}

	public String getName()
	{
		return "Template";
	}

}
