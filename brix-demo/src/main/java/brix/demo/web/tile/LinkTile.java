package brix.demo.web.tile;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

public class LinkTile implements Tile
{

    public LinkTile()
    {
    }

    public String getDisplayName()
    {
        return "Link tile";
    }

    public String getTypeName()
    {
        return getClass().getName();
    }

    public TileEditorPanel newEditor(String id, IModel<JcrNode> containerNode)
    {
        return new EmptyEditorPanel(id);
    }

    public Component newViewer(String id, IModel<JcrNode> tileNode, BrixPageParameters pageParameters)
    {
        return new LinkPanel(id, pageParameters);
    }


    public boolean requiresSSL(IModel<JcrNode> tileNode)
    {
        return false;
    }

}
