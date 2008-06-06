package brix.plugin.site.node.tilepage.markup;

import java.util.Map;

import brix.BrixNodeModel;
import brix.plugin.fragment.TileContainer;
import brix.plugin.site.node.tilepage.TileContainerNode;

/**
 * ComponentTag that that replaces the &lt;brix:tile&gt; tags.
 * 
 * @author Matej Knopp
 */
public class TileTag extends AbstractTileTag
{
    private final BrixNodeModel tileContainerNodeModel;

    public TileTag(String name, Type type, Map<String, String> attributeMap,
            TileContainerNode tileContainerNode, String tileName)
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
