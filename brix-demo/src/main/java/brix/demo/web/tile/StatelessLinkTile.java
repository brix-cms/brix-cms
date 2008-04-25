package brix.demo.web.tile;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
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

    public TileEditorPanel newEditor(String id, IModel<JcrNode> tileContainerNode)
    {
        return new EmptyEditorPanel(id);
    }

    public Component newViewer(String id, IModel<JcrNode> tileNodeModel, BrixPageParameters pageParameters)
    {
        return new StatelessLinkPanel(id, pageParameters);
    }

    public boolean requiresSSL(IModel<JcrNode> tileNodeModel)
    {
        return false;
    }

}
