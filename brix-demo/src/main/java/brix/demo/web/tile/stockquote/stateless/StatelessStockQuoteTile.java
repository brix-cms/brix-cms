package brix.demo.web.tile.stockquote.stateless;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.demo.web.tile.stockquote.stateful.StatefulStockQuoteTile;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.EmptyTileEditorPanel;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

/**
 * Stateless stock quote tile definition.
 * 
 * This tile is stateless which means it will not generate session if a page that only contains
 * stateless tiles will be accessed. Because this tile is stateless it does not take advantage of
 * Wicket's automatic state management and thus exposes extra work on the user to manage state. See
 * {@link StatelessStockQuotePanel} for details.
 * 
 * For a stateful variant of tile see {@link StatefulStockQuoteTile}
 * 
 * @author igor.vaynberg
 */
public class StatelessStockQuoteTile implements Tile
{

    /** {@inheritDoc} */
    public String getDisplayName()
    {
        return "Stateless Quote";
    }

    /** {@inheritDoc} */
    public String getTypeName()
    {
        return "brix.demo.StatelessStockQuoteTile";
    }

    /** {@inheritDoc} */
    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        // this tile has no configuration options
        return new EmptyTileEditorPanel(id);
    }

    /** {@inheritDoc} */
    public Component newViewer(String id, IModel<BrixNode> tileNode)
    {
        // create and return panel that will render the tile
        return new StatelessStockQuotePanel(id);
    }

    /** {@inheritDoc} */
    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

}
