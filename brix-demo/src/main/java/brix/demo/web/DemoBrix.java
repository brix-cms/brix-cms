package brix.demo.web;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Workspace;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.jackrabbit.api.JackrabbitNodeTypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import brix.util.StringInputStream;
import brix.web.tile.menu.MenuTile;
import brix.web.tile.pagetile.PageTile;
import brix.workspace.AbstractWorkspaceManager;
import brix.workspace.WorkspaceManager;
import brix.workspace.rmi.ClientWorkspaceManager;
import brix.workspace.rmi.ClientWorkspaceNodeTypeManager;


public class DemoBrix extends Brix
{

    private static Logger logger = LoggerFactory.getLogger(DemoBrix.class);

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
    protected void registerType(Workspace workspace, String typeName, boolean referenceable,
            boolean orderable) throws Exception
    {
        logger.info("Registering node type: {}", typeName);

        if (WicketApplication.USE_RMI)
        {
            NodeTypeManager manager = workspace.getNodeTypeManager();

            try
            {
                manager.getNodeType(typeName);
            }
            catch (NoSuchNodeTypeException e)
            {

                String type = "[" + typeName + "] > nt:unstructured ";

                if (referenceable)
                    type += ", mix:referenceable ";

                if (orderable)
                    type += "orderable ";

                type += " mixin";

                ClientWorkspaceNodeTypeManager client = new ClientWorkspaceNodeTypeManager(
                    "rmi://localhost:1099/jackrabbitwntm");

                client.registerNodeTypes(workspace.getName(), new StringInputStream(type),
                    JackrabbitNodeTypeManager.TEXT_X_JCR_CND, true);
            }

        }
        else
        {
            super.registerType(workspace, typeName, referenceable, orderable);
        }
    }

    @Override
    protected WorkspaceManager newWorkspaceManager()
    {
        if (WicketApplication.USE_RMI)
        {
            return new ClientWorkspaceManager("rmi://localhost:1099/jackrabbitwm");
        }
        else
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
                    return Arrays.asList(getSession(null).getWorkspace()
                        .getAccessibleWorkspaceNames());
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
