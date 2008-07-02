package brix.demo.web.tile.time;


import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

public class TimeTile implements Tile
{

    public Component newViewer(String id, IModel<BrixNode> tileNode, BrixPageParameters pageParameters)
    {
        return new TimeLabel(id, tileNode).setRenderBodyOnly(true);
    }

    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        return new TimeTileEditor(id, tileContainerNode);
    }

    public String getDisplayName()
    {
        return "Current Time Tile";
    }

    public String getTypeName()
    {
        return "brix.web.TimeTile";
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

}
