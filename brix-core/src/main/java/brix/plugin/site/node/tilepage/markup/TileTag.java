package brix.plugin.site.node.tilepage.markup;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.TileContainerNode;
import brix.plugin.site.node.tilepage.TileNodePlugin;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.markup.ComponentTag;
import brix.web.nodepage.markup.SimpleTag;

/**
 * ComponentTag that that replaces the &lt;brix:tile&gt; tags. 
 *  
 * @author Matej Knopp
 */
public class TileTag extends SimpleTag implements ComponentTag
{
	private final IModel<BrixNode> tileContainerNodeModel;
	private final String tileName;

	public TileTag(String name, Type type, Map<String, String> attributeMap, TileContainerNode tileContainerNode,
			String tileName)
	{
		super(name, type, attributeMap);
		this.tileContainerNodeModel = new BrixNodeModel(tileContainerNode);
		this.tileName = tileName;

		this.tileContainerNodeModel.detach();
	}

	public Component<?> getComponent(String id)
	{
		TileContainerNode tileContainerNode = (TileContainerNode) tileContainerNodeModel.getObject(); 
		BrixNode tileNode = tileContainerNode.getTile(tileName);
		tileContainerNodeModel.detach();
		if (tileNode != null)
		{
			TileNodePlugin plugin = (TileNodePlugin) SitePlugin.get().getNodePluginForNode(tileContainerNode);
			Tile tile = plugin.getTileOfType(TileContainerNode.getTileClassName(tileNode));
			BrixPageParameters parameters = BrixPageParameters.getCurrent();
			return tile.newViewer(id, new BrixNodeModel(tileNode), parameters);
		}
		else
		{
			return null;
		}
	}

	private final static AtomicLong atomicLong = new AtomicLong();
	
	private final static String PREFIX = "tile-";

	private String id;
	
	public String getUniqueId()
	{
		if (id == null)
		{
			id = PREFIX + atomicLong.incrementAndGet();
		}
		return id;
	}

}
