package brix.demo.web;

import brix.Brix;
import brix.auth.AuthorizationStrategy;
import brix.demo.web.tile.TimeTile;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.TileNodePlugin;
import brix.plugin.site.node.tilepage.TilePageNode;
import brix.plugin.site.node.tilepage.TilePageNodePlugin;
import brix.plugin.site.node.tilepage.TileTemplateNode;
import brix.plugin.site.node.tilepage.TileTemplateNodePlugin;
import brix.web.tile.menu.MenuTile;
import brix.web.tile.pagetile.PageTile;

public class DemoBrix extends Brix
{
    public DemoBrix()
    {
        TileNodePlugin plugin = new TilePageNodePlugin();
        addTiles(plugin);

        SitePlugin sitePlugin = SitePlugin.get(this);
        sitePlugin.registerNodePlugin(plugin);

        plugin = new TileTemplateNodePlugin();
        addTiles(plugin);
        sitePlugin.registerNodePlugin(plugin);

        getWrapperRegistry().registerWrapper(TilePageNode.class);
        getWrapperRegistry().registerWrapper(TileTemplateNode.class);

    }

    private void addTiles(TileNodePlugin plugin)
    {
        plugin.addTile(new TimeTile());
        plugin.addTile(new MenuTile());
        plugin.addTile(new PageTile());

        /*
         * plugin.addTile(new TreeMenuTile()); plugin.addTile(new LinkTile()); plugin.addTile(new
         * StatelessLinkTile()); plugin.addTile(new StatelessFormTile());
         * 
         */
    }

    @Override
    public AuthorizationStrategy newAuthorizationStrategy()
    {
        return new DemoAuthorizationStrategy();
    }
}
