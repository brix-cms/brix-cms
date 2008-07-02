package brix.demo.web;

import brix.Brix;
import brix.Plugin;
import brix.auth.AuthorizationStrategy;
import brix.config.BrixConfig;
import brix.demo.web.tile.TimeTile;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.prototype.PrototypePlugin;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.snapshot.SnapshotPlugin;

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

        // register plugins
        config.getRegistry().register(Plugin.POINT, new MenuPlugin(this));
        config.getRegistry().register(Plugin.POINT, new SnapshotPlugin(this));
        config.getRegistry().register(Plugin.POINT, new PrototypePlugin(this));

        // register tiles
        getConfig().getRegistry().register(Tile.POINT, new TimeTile());
    }

    /** {@inheritDoc} */
    @Override
    public AuthorizationStrategy newAuthorizationStrategy()
    {
        // register our simple demo auth strategy
        return new DemoAuthorizationStrategy();
    }
}
