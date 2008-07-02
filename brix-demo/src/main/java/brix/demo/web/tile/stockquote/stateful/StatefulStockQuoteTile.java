package brix.demo.web.tile.stockquote.stateful;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.demo.web.tile.stockquote.stateless.StatelessStockQuoteTile;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.EmptyTileEditorPanel;
import brix.plugin.site.page.tile.admin.TileEditorPanel;
import brix.web.nodepage.BrixPageParameters;

/**
 * Stateful stock quote tile definition.
 * 
 * This tile is stateful because its {@link StatefulStockQuotePanel} keeps state like a regular
 * Wicket component and thus gains all the advantages of Wicket's automatic state management.
 * 
 * Brix supports both stateful and stateless tiles, for a stateless variant of this tile see
 * {@link StatelessStockQuoteTile}
 * 
 * @author igor.vaynberg
 */
public class StatefulStockQuoteTile implements Tile
{

    /** {@inheritDoc} */
    public String getDisplayName()
    {
        return "Stateful Quote";
    }

    /** {@inheritDoc} */
    public String getTypeName()
    {
        return "brix.demo.StatefulStockQuoteTile";
    }

    /** {@inheritDoc} */
    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        // this tile has no config options
        return new EmptyTileEditorPanel(id);
    }

    /** {@inheritDoc} */
    public Component newViewer(String id, IModel<BrixNode> tileNode)
    {
        // create and return a panel that will render the tile
        return new StatefulStockQuotePanel(id);
    }

    /** {@inheritDoc} */
    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

}
