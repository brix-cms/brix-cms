package brix.plugin.site.page.tile.admin;

import brix.jcr.wrapper.BrixNode;

/**
 * Provides a default empty editor panel for tiles that have no configuration options
 * 
 * @author igor.vaynberg
 * 
 */
public class EmptyTileEditorPanel extends TileEditorPanel
{
    /**
     * Constructor
     * 
     * @param id
     */
    public EmptyTileEditorPanel(String id)
    {
        super(id);
    }

    /** {@inheritDoc} */
    @Override
    public void load(BrixNode node)
    {
        // noop
    }

    /** {@inheritDoc} */
    @Override
    public void save(BrixNode node)
    {// noop
    }

}
