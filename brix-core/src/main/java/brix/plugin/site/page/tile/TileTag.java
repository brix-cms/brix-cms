package brix.plugin.site.page.tile;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.markup.tag.ComponentTag;
import brix.markup.tag.simple.SimpleTag;
import brix.markup.variable.VariableKeyProvider;
import brix.plugin.site.page.AbstractContainer;
import brix.web.nodepage.BrixPageParameters;

/**
 * Base class for tags that represent {@link Tile}s
 * 
 * @author ivaynberg, Matej Knopp
 */
public class TileTag extends SimpleTag
        implements
            ComponentTag,
            VariableKeyProvider
{
    /** name of tile this tag is attached to */
    private final String tileName;

    private final BrixNodeModel tileContainerNodeModel;
    
    /**
     * Constructor
     * 
     * @param name
     * @param type
     * @param attributeMap
     * @param tileName
     */
    public TileTag(String name, Type type, Map<String, String> attributeMap, AbstractContainer tileContainerNode, String tileName)
    {
        super(name, type, attributeMap);
        this.tileName = tileName;
        tileContainerNodeModel = new BrixNodeModel(tileContainerNode);
        tileContainerNodeModel.detach();
    }

    /**
     * @return tile container that contains the tile
     */
    protected AbstractContainer getTileContainer()
    {
        AbstractContainer container = (AbstractContainer)tileContainerNodeModel.getObject();
        tileContainerNodeModel.detach();
        return container;
    }

    /**
     * @return name of tile
     */
    public String getTileName()
    {
        return tileName;
    }

    /** {@inheritDoc} */
    public Component< ? > getComponent(String id, IModel<BrixNode> pageNodeModel)
    {
        AbstractContainer container = getTileContainer();
        BrixNode tileNode = container.getTileNode(tileName);

        if (tileNode != null)
        {
            Tile tile = Tile.Helper.getTileOfType(TileContainerFacet.getTileClassName(tileNode),
                tileNode.getBrix());
            BrixPageParameters parameters = BrixPageParameters.getCurrent();
            return tile.newViewer(id, new BrixNodeModel(tileNode), parameters);
        }
        else
        {
            return null;
        }
    }

    /** {@inheritDoc} */
    public Collection<String> getVariableKeys()
    {

        BrixNode tileNode = getTileContainer().tiles().getTile(tileName);
        if (tileNode != null)
        {
            Tile tile = Tile.Helper.getTileOfType(TileContainerFacet.getTileClassName(tileNode),
                tileNode.getBrix());
            if (tile instanceof VariableKeyProvider)
            {
                return ((VariableKeyProvider)tile).getVariableKeys();
            }
        }
        return null;
    }

    private final static AtomicLong atomicLong = new AtomicLong();

    private final static String PREFIX = "tile-";

    private String id;

    /**
     * return unique id of this tag
     */
    public String getUniqueTagId()
    {
        if (id == null)
        {
            id = PREFIX + atomicLong.incrementAndGet();
        }
        return id;
    }

}
