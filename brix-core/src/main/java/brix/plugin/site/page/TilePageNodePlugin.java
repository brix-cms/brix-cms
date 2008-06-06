package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.page.admin.CreateTilePagePanel;
import brix.web.admin.navigation.NavigationAwarePanel;

public class TilePageNodePlugin extends TileNodePlugin
{

	public static final String TYPE = Brix.NS_PREFIX + "tilePage";

	public TilePageNodePlugin(Brix brix)
	{
		super(brix);
	}

	@Override
	public NodeConverter getConverterForNode(BrixNode node)
	{
		if (TileTemplateNodePlugin.TYPE.equals(((BrixNode) node).getNodeType()))
			return new FromTemplateConverter(getNodeType());
		else
			return super.getConverterForNode(node);
	}

	private static class FromTemplateConverter extends SetTypeConverter
	{
		public FromTemplateConverter(String type)
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
		return "Tile Page";
	}
}
