package brix.plugin.site.page.markup;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.fragment.TileContainer;
import brix.plugin.site.page.TileContainerFacet;
import brix.plugin.site.page.admin.Tile;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.markup.ComponentTag;
import brix.web.nodepage.markup.simple.SimpleTag;
import brix.web.nodepage.markup.variable.VariableKeyProvider;

/**
 * Base class for tags that represent {@link Tile}s
 * 
 * @author ivaynberg
 */
public abstract class AbstractTileTag extends SimpleTag
        implements
            ComponentTag,
            VariableKeyProvider
{
    /** name of tile this tag is attached to */
    private final String tileName;

    /**
     * Constructor
     * 
     * @param name
     * @param type
     * @param attributeMap
     * @param tileName
     */
    public AbstractTileTag(String name, Type type, Map<String, String> attributeMap, String tileName)
    {
        super(name, type, attributeMap);
        this.tileName = tileName;
    }

    /**
     * @return tile container that contains the tile
     */
    protected abstract TileContainer getTileContainer();

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
        TileContainer container = getTileContainer();
        BrixNode tileNode = container.tiles().getTile(tileName);

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
