package brix.demo.web.tile;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

public class StatelessFormTile implements Tile
{

    public StatelessFormTile()
    {
    }

    public String getDisplayName()
    {
        return "stateless Form tile";
    }

    public String getTypeName()
    {
        return getClass().getName();
    }

    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        return new EmptyEditorPanel(id);
    }

    public Component newViewer(String id, IModel<BrixNode> tileNodeModel, BrixPageParameters pageParameters)
    {
        return new StatelessFormPanel(id);
    }

    public boolean requiresSSL(IModel<BrixNode> tileNodeModel)
    {
        return false;
    }

}
