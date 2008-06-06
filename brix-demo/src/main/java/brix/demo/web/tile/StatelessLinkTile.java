package brix.demo.web.tile;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.admin.Tile;
import brix.plugin.site.page.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

public class StatelessLinkTile implements Tile
{

    public StatelessLinkTile()
    {
    }

    public String getDisplayName()
    {
        return "stateless Link tile";
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
        return new StatelessLinkPanel(id, pageParameters);
    }

    public boolean requiresSSL(IModel<BrixNode> tileNodeModel)
    {
        return false;
    }

}
