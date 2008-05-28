package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;
import brix.web.nodepage.BrixPageParameters;

public interface Tile
{
	public static ExtensionPoint<Tile> POINT = new ExtensionPoint<Tile>()
	{
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity()
		{
			return Multiplicity.COLLECTION;
		}
		public String getUuid()
		{
			return Tile.class.getName();
		}
	};

    // TODO remove tilePageParameters param in favor of PageParametersAware
    Component newViewer(String id, IModel<BrixNode> tileNode, BrixPageParameters tilePageParameters);

    TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode);

    String getDisplayName();

    String getTypeName();

    boolean requiresSSL(IModel<BrixNode> tileNode);
}
