package brix.web.tile.unknown;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

public class UnknownTile implements Tile
{

    public String getDisplayName()
    {
        return "Unknown";
    }

    public String getTypeName()
    {
        return UnknownTile.class.getName();
    }

    private static class Editor extends TileEditorPanel
    {
        public Editor(String id)
        {
            super(id);
        }

        @Override
        public void load(BrixNode node)
        {

        }

        @Override
        public void save(BrixNode node)
        {

        }
    };

    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        return new Editor(id);
    }

    public Component newViewer(String id, IModel<BrixNode> tileNode, BrixPageParameters tilePageParameters)
    {
        return new Label(id, "Unknown Tile");
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

    public static final UnknownTile INSTANCE = new UnknownTile();
}
