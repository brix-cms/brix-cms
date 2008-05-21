package brix.demo.web;

import java.util.Arrays;
import java.util.List;

import brix.Brix;
import brix.auth.AuthorizationStrategy;
import brix.demo.web.tile.TimeTile;
import brix.jcr.JcrSessionFactory;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.tilepage.TileNodePlugin;
import brix.plugin.site.node.tilepage.TilePageNode;
import brix.plugin.site.node.tilepage.TilePageNodePlugin;
import brix.plugin.site.node.tilepage.TileTemplateNode;
import brix.plugin.site.node.tilepage.TileTemplateNodePlugin;
import brix.web.tile.menu.MenuTile;
import brix.web.tile.pagetile.PageTile;
import brix.workspace.AbstractWorkspaceManager;
import brix.workspace.WorkspaceManager;

;

public class DemoBrix extends Brix
{
    public DemoBrix(JcrSessionFactory sf)
    {
        super(sf);
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

    @Override
    protected WorkspaceManager newWorkspaceManager()
    {
        AbstractWorkspaceManager manager = new AbstractWorkspaceManager()
        {

            @Override
            protected void createWorkspace(String workspaceName)
            {
                JcrSession session = getSession(null);
                DemoBrix.this.createWorkspace(session, workspaceName);
            }

            @Override
            protected List<String> getAccessibleWorkspaceNames()
            {
                return Arrays.asList(getSession(null).getWorkspace().getAccessibleWorkspaceNames());
            }

            @Override
            protected JcrSession getSession(String workspaceName)
            {
                return getCurrentSession(workspaceName);
            }

        };
        manager.initialize();
        return manager;
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
