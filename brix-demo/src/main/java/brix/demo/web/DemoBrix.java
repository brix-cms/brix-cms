package brix.demo.web;

import brix.Brix;
import brix.auth.AuthorizationStrategy;
import brix.config.BrixConfig;
import brix.demo.web.tile.TimeTile;
import brix.plugin.site.page.tile.Tile;

/**
 * Subclass of {@link Brix} that configures demo-specific settings such as plugins, etc.
 * 
 * @author igor.vaynberg
 * 
 */
public class DemoBrix extends Brix
{
    public DemoBrix(BrixConfig config)
    {
        super(config);
        addTiles();
    }

    private void addTile(Tile tile)
    {
        getConfig().getRegistry().register(Tile.POINT, tile);
    }

    private void addTiles()
    {
        addTile(new TimeTile());
    }

    @Override
    public AuthorizationStrategy newAuthorizationStrategy()
    {
        return new DemoAuthorizationStrategy();
    }
}
