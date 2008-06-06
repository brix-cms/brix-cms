package brix.demo.web.tile;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.admin.Tile;
import brix.plugin.site.page.admin.TileEditorPanel;
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

    public TileEditorPanel newEditor(String id, IModel<BrixNode> containerNode)
    {
        return new EmptyEditorPanel(id);
    }

    public Component newViewer(String id, IModel<BrixNode> tileNode, BrixPageParameters pageParameters)
    {
        return new LinkPanel(id, pageParameters);
    }


    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

}
