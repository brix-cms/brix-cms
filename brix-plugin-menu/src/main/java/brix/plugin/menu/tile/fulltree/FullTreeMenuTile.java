package brix.plugin.menu.tile.fulltree;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.TileEditorPanel;

public class FullTreeMenuTile implements Tile
{

	public String getDisplayName()
	{
		return "Menu - Full Tree";
	}

	public String getTypeName()
	{
		return "brix:plugin:menu:tile:fulltree";
	}

	public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
	{
		return new FullTreeMenuEditor(id, tileContainerNode);
	}

	public Component newViewer(String id, IModel<BrixNode> tileNode)
	{
		return new MenuRenderer(id, tileNode);
	}

	public boolean requiresSSL(IModel<BrixNode> tileNode)
	{
		return false;
	}

}
