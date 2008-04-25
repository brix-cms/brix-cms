package brix.demo.web.tile;

import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;

public class EmptyEditorPanel extends TileEditorPanel
{

    public EmptyEditorPanel(String id)
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

}
