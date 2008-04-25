package brix.demo.web.tile;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.admin.Tile;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
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

    public TileEditorPanel newEditor(String id, IModel<JcrNode> tileContainerNode)
    {
        return new EmptyEditorPanel(id);
    }

    public Component newViewer(String id, IModel<JcrNode> tileNodeModel, BrixPageParameters pageParameters)
    {
        return new StatelessFormPanel(id);
    }

    public boolean requiresSSL(IModel<JcrNode> tileNodeModel)
    {
        return false;
    }

}
