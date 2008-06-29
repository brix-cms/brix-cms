package brix.plugin.site.page.tile;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.registry.ExtensionPoint;
import brix.registry.ExtensionPointRegistry;
import brix.web.nodepage.BrixPageParameters;
import brix.web.tile.unknown.UnknownTile;

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

    public static class Helper
    {
        public static Collection<Tile> getTiles(Brix brix)
        {
            final ExtensionPointRegistry registry = brix.getConfig().getRegistry();
            return registry.lookupCollection(Tile.POINT);
        }

        public static Tile getTileOfType(String type, Brix brix)
        {
            for (Tile t : getTiles(brix))
            {
                if (t.getTypeName().equals(type))
                {
                    return t;
                }
            }
            return UnknownTile.INSTANCE;
        }

    }


    // TODO remove tilePageParameters param in favor of PageParametersAware
    Component newViewer(String id, IModel<BrixNode> tileNode,
            BrixPageParameters tilePageParameters);

    TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode);

    String getDisplayName();

    String getTypeName();

    boolean requiresSSL(IModel<BrixNode> tileNode);
}
