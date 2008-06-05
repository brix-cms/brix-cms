package brix.plugin.site.node.tilepage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.exception.NodeNotFoundException;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrPropertyIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.plugin.site.node.tilepage.markup.TilePageMarkupSource;
import brix.web.nodepage.markup.Item;
import brix.web.nodepage.markup.variable.VariableKeyProvider;
import brix.web.nodepage.markup.variable.VariableTransformer;
import brix.web.nodepage.markup.variable.VariableValueProvider;

public abstract class TileContainerNode extends BrixFileNode implements VariableValueProvider, VariableKeyProvider
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

	public BrixNode getTile(String id)
	{
		return getTile(this, id);
	}

	public static BrixNode getTile(BrixNode container, String id)
	{
		if (id == null)
		{
			throw new IllegalArgumentException("tile id cannot be null");
		}
		JcrNodeIterator iterator = container.getNodes(TILE_NODE_NAME);
		while (iterator.hasNext())
		{
			BrixNode node = (BrixNode) iterator.nextNode();
			if (node.isNodeType(JCR_TYPE_BRIX_TILE) && id.equals(getTileId(node)))
			{
				return node;
			}
		}
		return null;
	}

	public List<BrixNode> getTileNodes()
	{
		return getTileNodes(this);
	}

	public static List<BrixNode> getTileNodes(BrixNode container)
	{
		List<BrixNode> result = new ArrayList<BrixNode>();
		JcrNodeIterator iterator = container.getNodes(TILE_NODE_NAME);
		while (iterator.hasNext())
		{
			BrixNode node = (BrixNode) iterator.nextNode();
			if (node.isNodeType(JCR_TYPE_BRIX_TILE))
			{
				result.add(node);
			}
		}
		return result;
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

	public TileTemplateNode getTemplate()
	{
		if (hasProperty(Properties.TEMPLATE))
		{
			return (TileTemplateNode) getProperty(Properties.TEMPLATE).getNode();
		}
		else
		{
			return null;
		}
	}

	public void setTemplate(BrixNode node)
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
			BrixNode node = (BrixNode) SitePlugin.get().nodeForPath(this, path);

			if (node == null)
			{
				throw new NodeNotFoundException("No node found on path '" + path + "'.");
			}

			setTemplate((BrixNode) node);
		}
	}

	public String getTemplatePath()
	{
		BrixNode template = getTemplate();
		return template != null ? SitePlugin.get().pathForNode(template) : null;
	}

	public BrixNode createTile(String tileId, String typeName)
	{
		return createTile(this, tileId, typeName);
	}

	public static BrixNode createTile(BrixNode container, String tileId, String typeName)
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
		// throw new IllegalArgumentException("Argument 'tileId' is not a valid
		// node name.");
		// }
		// if (hasNode(tileId))
		// {
		// throw new BrixException("Tile with id '" + tileId + "' already
		// exists.");
		// }

		BrixNode tile = (BrixNode) container.addNode(TILE_NODE_NAME, JCR_TYPE_BRIX_TILE);

		tile.setProperty(Properties.TILE_ID, tileId);
		tile.setProperty(Properties.TILE_CLASS, typeName);

		return tile;
	}

	public void setRequiresSSL(boolean value)
	{
		if (value == false)
		{
			setProperty(Properties.REQUIRES_SSL, (String) null);
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
	public Protocol getRequiredProtocol()
	{
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
		return (TileNodePlugin) SitePlugin.get().getNodePluginForNode(this);
	}

	private boolean anyTileRequiresSSL()
	{
		List<BrixNode> tiles = getTileNodes();
		for (BrixNode tileNode : tiles)
		{
			String className = getTileClassName(tileNode);
			Tile tile = getNodePlugin().getTileOfType(className);
			IModel<BrixNode> tileNodeModel = new BrixNodeModel(tileNode);
			if (tile.requiresSSL(tileNodeModel))
				return true;
		}
		return false;
	};

	private static final String VARIABLES_NODE_NAME = Brix.NS_PREFIX + "variables";

	public String getVariableValue(String key)
	{
		if (hasNode(VARIABLES_NODE_NAME))
		{
			JcrNode node = getNode(VARIABLES_NODE_NAME);
			if (node.hasProperty(key))
			{
				return node.getProperty(key).getString();
			}
		}
		TileTemplateNode template = getTemplate();
		if (template != null)
		{
			return template.getVariableValue(key);
		}
		return null;
	}

	public void setVariableValue(String key, String value)
	{
		final JcrNode node;
		if (hasNode(VARIABLES_NODE_NAME))
		{
			node = getNode(VARIABLES_NODE_NAME);
		}
		else
		{
			node = addNode(VARIABLES_NODE_NAME, "nt:unstructured");
		}
		node.setProperty(key, value);
		save();
	}

	/**
	 * Returns collection of possible variable keys for this node.
	 */
	public Collection<String> getVariableKeys()
	{
		Set<String> keys = new HashSet<String>();
		TilePageMarkupSource source = new TilePageMarkupSource(this);
		VariableTransformer transfomer = new VariableTransformer(source, this);
		Item i = transfomer.nextMarkupItem();
		while (i != null)
		{
			if (i instanceof VariableKeyProvider)
			{
				Collection<String> k = ((VariableKeyProvider) i).getVariableKeys();
				if (k != null)
				{
					keys.addAll(k);
				}
			}
			i = transfomer.nextMarkupItem();
		}
		return keys;
	}

	public List<String> getSavedVariableKeys()
	{
		if (hasNode(VARIABLES_NODE_NAME))
		{
			JcrNode node = getNode(VARIABLES_NODE_NAME);
			List<String> result = new ArrayList<String>();
			JcrPropertyIterator i = node.getProperties();
			while (i.hasNext())
			{
				result.add(i.nextProperty().getName());
			}
			return result;
		}
		else
		{
			return Collections.emptyList();
		}
	}

}
