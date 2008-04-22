package brix.web.tile.unknown;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
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
        public void load(JcrNode node)
        {

        }

        @Override
        public void save(JcrNode node)
        {

        }
    };

    public TileEditorPanel newEditor(String id, IModel<JcrNode> tileContainerNode)
    {
        return new Editor(id);
    }

    public Component newViewer(String id, IModel<JcrNode> tileNode, BrixPageParameters tilePageParameters)
    {
        return new Label(id, "Unknown Tile");
    }

    public boolean requiresSSL(IModel<JcrNode> tileNode)
    {
        return false;
    }

    public static final UnknownTile INSTANCE = new UnknownTile();
}
