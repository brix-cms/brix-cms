package brix.plugin.site.node.tilepage;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.node.tilepage.admin.Tile;

/**
 * Helper for managing node's tile collection
 * 
 * @author ivaynberg
 * 
 */
public class TileContainerFacet
{
    /** Name of the Tile nodes(s) */
    public static final String TILE_NODE_NAME = Brix.NS_PREFIX + "tile";

    /** JCR type of Tile nodes */
    public static final String JCR_TYPE_BRIX_TILE = Brix.NS_PREFIX + "tile";


    private final BrixNode container;


    private static class Properties
    {
        public static final String TILE_ID = Brix.NS_PREFIX + "tileId";
        public static final String TILE_CLASS = Brix.NS_PREFIX + "tileClass";
    }


    public TileContainerFacet(BrixNode container)
    {
        this.container = container;
    }


    public BrixNode getTile(String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("tile id cannot be null");
        }
        JcrNodeIterator iterator = container.getNodes(TILE_NODE_NAME);
        while (iterator.hasNext())
        {
            BrixNode node = (BrixNode)iterator.nextNode();
            if (node.isNodeType(JCR_TYPE_BRIX_TILE) && id.equals(getTileId(node)))
            {
                return node;
            }
        }
        return null;
    }

    public static String getTileId(BrixNode tile)
    {
        if (tile.hasProperty(Properties.TILE_ID))
        {
            return tile.getProperty(Properties.TILE_ID).getString();
        }
        else
        {
            return null;
        }
    }

    public static String getTileClassName(BrixNode tile)
    {
        if (tile.hasProperty(Properties.TILE_CLASS))
        {
            return tile.getProperty(Properties.TILE_CLASS).getString();
        }
        else
        {
            return null;
        }
    }

    public List<BrixNode> getTileNodes()
    {
        List<BrixNode> result = new ArrayList<BrixNode>();
        JcrNodeIterator iterator = container.getNodes(TILE_NODE_NAME);
        while (iterator.hasNext())
        {
            BrixNode node = (BrixNode)iterator.nextNode();
            if (node.isNodeType(JCR_TYPE_BRIX_TILE))
            {
                result.add(node);
            }
        }
        return result;
    }


    public BrixNode createTile(String tileId, String typeName)
    {
        if (tileId == null)
        {
            throw new IllegalArgumentException("Argument 'tileId' may not be null.");
        }
        if (typeName == null)
        {
            throw new IllegalArgumentException("Argument 'typeName' may not be null.");
        }

        // TODO this check needs to be fixed?
        // if (isValidNodeName(tileId) == false)
        // {
        // throw new IllegalArgumentException("Argument 'tileId' is not a valid node name.");
        // }
        // if (hasNode(tileId))
        // {
        // throw new BrixException("Tile with id '" + tileId + "' already exists.");
        // }

        BrixNode tile = (BrixNode)container.addNode(TILE_NODE_NAME, JCR_TYPE_BRIX_TILE);

        tile.setProperty(Properties.TILE_ID, tileId);
        tile.setProperty(Properties.TILE_CLASS, typeName);

        return tile;
    }

    public String getTileClassName(String tileId)
    {
        BrixNode tile = getTile(tileId);
        if (tile != null)
        {
            return getTileClassName(tile);
        }
        else
        {
            return null;
        }
    }

    public boolean anyTileRequiresSSL()
    {
        List<BrixNode> tiles = getTileNodes();
        for (BrixNode tileNode : tiles)
        {
            String className = TileContainerFacet.getTileClassName(tileNode);
            Tile tile = Tile.Helper.getTileOfType(className, Brix.get());
            IModel<BrixNode> tileNodeModel = new BrixNodeModel(tileNode);
            if (tile.requiresSSL(tileNodeModel))
                return true;
        }
        return false;
    };


}
