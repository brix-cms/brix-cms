package brix.demo.web.tile;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.admin.TileEditorPanel;

public class EmptyEditorPanel extends TileEditorPanel
{

    public EmptyEditorPanel(String id)
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

}
