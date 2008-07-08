package brix.demo.web.tile.guestbook;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.EmptyTileEditorPanel;
import brix.plugin.site.page.tile.admin.TileEditorPanel;

public class GuestBookTile implements Tile
{
    public String getDisplayName()
    {
        return "Guest Book";
    }

    public String getTypeName()
    {
        return "brix.tile.GuestBook";
    }

    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        return new EmptyTileEditorPanel(id);
    }

    public Component newViewer(String id, IModel<BrixNode> tileNode)
    {
        return new GuestBookPanel(id, tileNode);
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

}
