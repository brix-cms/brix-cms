package brix.demo.web;

import brix.Brix;
import brix.Plugin;
import brix.auth.AuthorizationStrategy;
import brix.config.BrixConfig;
import brix.demo.web.tile.guestbook.GuestBookTile;
import brix.demo.web.tile.stockquote.stateful.StatefulStockQuoteTile;
import brix.demo.web.tile.stockquote.stateless.StatelessStockQuoteTile;
import brix.demo.web.tile.time.TimeTile;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.snapshot.SnapshotPlugin;
import brix.plugin.webdavurl.WebdavUrlPlugin;

/**
 * Subclass of {@link Brix} that configures demo-specific settings such as plugins, tiles, etc.
 * 
 * @author igor.vaynberg
 * 
 */
public class DemoBrix extends Brix
{
    /**
     * Constructor
     * 
     * @param config
     */
    public DemoBrix(BrixConfig config)
    {
        super(config);

        config.getRegistry().register(Tile.POINT, new GuestBookTile());

        // register plugins
        config.getRegistry().register(Plugin.POINT, new MenuPlugin(this));
        config.getRegistry().register(Plugin.POINT, new SnapshotPlugin(this));
        config.getRegistry().register(Plugin.POINT, new WebdavUrlPlugin());


        // register tiles
        config.getRegistry().register(Tile.POINT, new TimeTile());
        config.getRegistry().register(Tile.POINT, new StatefulStockQuoteTile());
        config.getRegistry().register(Tile.POINT, new StatelessStockQuoteTile());
    }

    /** {@inheritDoc} */
    @Override
    public AuthorizationStrategy newAuthorizationStrategy()
    {
        // register our simple demo auth strategy
        return new DemoAuthorizationStrategy();
    }
}
