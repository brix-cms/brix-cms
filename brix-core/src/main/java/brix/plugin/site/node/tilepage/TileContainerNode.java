package brix.plugin.site.node.tilepage;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.exception.BrixException;
import brix.exception.NodeNotFoundException;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.admin.Tile;

public abstract class TileContainerNode extends BrixFileNode
{

    public TileContainerNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    /** Name of markup attribute used to identify tile id inside brix:tile tag */
    public static final String MARKUP_TILE_ID = "id";

    /** Name of the Tile nodes(s) */
    public static final String TILE_NODE_NAME = Brix.NS_PREFIX + "tile";

    /** JCR type of Tile nodes */
    public static final String JCR_TYPE_BRIX_TILE = Brix.NS_PREFIX + "tile";


    private static class Properties
    {
        public static final String TITLE = Brix.NS_PREFIX + "title";
        public static final String TEMPLATE = Brix.NS_PREFIX + "template";
        public static final String REQUIRES_SSL = Brix.NS_PREFIX + "requiresSSL";

        public static final String TILE_ID = Brix.NS_PREFIX + "tileId";
        public static final String TILE_CLASS = Brix.NS_PREFIX + "tileClass";

    }


    public String getTitle()
    {
        if (hasProperty(Properties.TITLE))
            return getProperty(Properties.TITLE).getString();
        else
            return null;
    }

    public void setTitle(String title)
    {
        setProperty(Properties.TITLE, title);
    }

    public JcrNode getTile(String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("tile id cannot be null");
        }
        JcrNodeIterator iterator = getNodes(TILE_NODE_NAME);
        while (iterator.hasNext())
        {
            JcrNode node = iterator.nextNode();
            if (node.isNodeType(JCR_TYPE_BRIX_TILE) && id.equals(getTileId(node)))
            {
                return node;
            }
        }
        return null;
    }

    public List<JcrNode> getTileNodes()
    {
        List<JcrNode> result = new ArrayList<JcrNode>();
        JcrNodeIterator iterator = getNodes(TILE_NODE_NAME);
        while (iterator.hasNext())
        {
            JcrNode node = iterator.nextNode();
            if (node.isNodeType(JCR_TYPE_BRIX_TILE))
            {
                result.add(node);
            }
        }
        return result;
    }

    public static String getTileId(JcrNode tile)
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

    public static String getTileClassName(JcrNode tile)
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

    public String getTileClassName(String tileId)
    {
        JcrNode tile = getTile(tileId);
        if (tile != null)
        {
            return getTileClassName(tile);
        }
        else
        {
            return null;
        }
    }

    public JcrNode getTemplate()
    {
        if (hasProperty(Properties.TEMPLATE))
        {
            return getProperty(Properties.TEMPLATE).getNode();
        }
        else
        {
            return null;
        }
    }

    public void setTemplate(JcrNode node)
    {
        setProperty(Properties.TEMPLATE, node);
    }

    public void setTemplatePath(String path)
    {
        if (path == null)
        {
            setTemplate(null);
        }
        else
        {
            JcrNode node = SitePlugin.get().nodeForPath(this, path);

            if (node == null)
            {
                throw new NodeNotFoundException("No node found on path '" + path + "'.");
            }

            setTemplate((JcrNode)node);
        }
    }

    public String getTemplatePath()
    {
        JcrNode template = getTemplate();
        return template != null ? SitePlugin.get().pathForNode(template) : null;
    }

    public JcrNode createTile(String tileId, String typeName)
    {
        if (tileId == null)
        {
            throw new IllegalArgumentException("Argument 'tileId' may not be null.");
        }
        if (typeName == null)
        {
            throw new IllegalArgumentException("Argument 'typeName' may not be null.");
        }
        if (isValidNodeName(tileId) == false)
        {
            throw new IllegalArgumentException("Argument 'tileId' is not a valid node name.");
        }

        if (hasNode(tileId))
        {
            throw new BrixException("Tile with id '" + tileId + "' already exists.");
        }

        JcrNode tile = addNode(TILE_NODE_NAME, JCR_TYPE_BRIX_TILE);

        tile.setProperty(Properties.TILE_ID, tileId);
        tile.setProperty(Properties.TILE_CLASS, typeName);

        return tile;
    }

    public void setRequiresSSL(boolean value)
    {
        if (value == false)
        {
            setProperty(Properties.REQUIRES_SSL, (String)null);
        }
        else
        {
            setProperty(Properties.REQUIRES_SSL, true);
        }
    }

    public boolean isRequiresSSL()
    {
        if (hasProperty(Properties.REQUIRES_SSL))
        {
            return getProperty(Properties.REQUIRES_SSL).getBoolean();
        }
        else
        {
            return false;
        }
    }

    public boolean requiresSSL()
    {
        return isRequiresSSL() || anyTileRequiresSSL();
    }
    
    @Override
    public Protocol getRequiredProtocol() {
    	if (requiresSSL()) 
    	{
    		return Protocol.HTTPS;
    	} 
    	else
    	{
    		return Protocol.HTTP;
    	}
    }

    public TileNodePlugin getNodePlugin()
    {
        return (TileNodePlugin)SitePlugin.get().getNodePluginForNode(this);
    }

    private boolean anyTileRequiresSSL()
    {
        List<JcrNode> tiles = getTileNodes();
        for (JcrNode tileNode : tiles)
        {
            String className = getTileClassName(tileNode);
            Tile tile = getNodePlugin().getTileOfType(className);
            IModel<JcrNode> tileNodeModel = new BrixNodeModel(tileNode);
            if (tile.requiresSSL(tileNodeModel))
                return true;
        }
        return false;
    };

}
