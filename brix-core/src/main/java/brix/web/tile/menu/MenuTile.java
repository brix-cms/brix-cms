package brix.web.tile.menu;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

public class MenuTile implements Tile
{
    public static final String TYPE_NAME = MenuTile.class.getName();

    public String getDisplayName()
    {
        return "Menu";
    }

    public String getTypeName()
    {
        return TYPE_NAME;
    }

    public TileEditorPanel< ? > newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        return new MenuTileEditor(id, tileContainerNode);
    }

    public Component< ? > newViewer(String id, IModel<BrixNode> tileNode,
            BrixPageParameters tilePageParameters)
    {
        return new MenuRenderer(id, tileNode);
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
