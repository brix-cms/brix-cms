package brix.plugin.site.page.tile;

import java.util.Map;

import brix.BrixNodeModel;
import brix.plugin.site.page.AbstractContainer;
import brix.plugin.site.page.fragment.TileContainer;

/**
 * ComponentTag that that replaces the &lt;brix:tile&gt; tags.
 * 
 * @author Matej Knopp
 */
public class TileTag extends AbstractTileTag
{
    private final BrixNodeModel tileContainerNodeModel;

    public TileTag(String name, Type type, Map<String, String> attributeMap,
            AbstractContainer tileContainerNode, String tileName)
    {
        super(name, type, attributeMap, tileName);
        tileContainerNodeModel = new BrixNodeModel(tileContainerNode);
        tileContainerNodeModel.detach();
    }

    @Override
    protected TileContainer getTileContainer()
    {
        TileContainer container = (TileContainer)tileContainerNodeModel.getObject();
        tileContainerNodeModel.detach();
        return container;
    }
}
