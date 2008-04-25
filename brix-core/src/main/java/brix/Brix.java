package brix;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.api.JackrabbitNodeTypeManager;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.wicket.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.auth.AuthorizationStrategy;
import brix.jcr.JcrEventListener;
import brix.jcr.SessionProvider;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.event.EventUtil;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.WrapperRegistry;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.folder.FolderNode;
import brix.plugin.site.node.folder.FolderNodePlugin;
import brix.plugin.site.node.tilepage.TileContainerNode;
import brix.plugin.site.node.tilepage.TilePageNodePlugin;
import brix.plugin.site.node.tilepage.TileTemplateNodePlugin;
import brix.util.StringInputStream;
import brix.web.nodepage.PageParametersAwareEnabler;

public abstract class Brix
{
    public static final String NS = "brix";
    public static final String NS_PREFIX = NS + ":";

    public Brix()
    {    
        wrapperRegistry.registerWrapper(FolderNode.class);
        
        registerPlugin(new SitePlugin());
        registerPlugin(new MenuPlugin());
    }


    /**
     * Performs any {@link Application} specific initialization
     * 
     * @param application
     */
    public void attachTo(Application application)
    {
        if (application == null)
        {
            throw new IllegalArgumentException("Application cannot be null");
        }
        /*
         * XXX we are coupling to nodepage plugin here instead of using the usual register mechanism -
         * we either need to make plugins application aware so they can install their own listeners
         * or have some brix-level registery
         */
        application.addPreComponentOnBeforeRenderListener(new PageParametersAwareEnabler());
    }    

    public List<String> getVisibleWorkspaces() {
        return getAvailableWorkspaces(BrixRequestCycle.Locator.getSession(null));
    }
    
    public List<String> getAvailableWorkspaces(JcrSession session)
    {
        return Arrays.asList(session.getWorkspace()
                .getAccessibleWorkspaceNames());
    }


    public void createWorkspace(JcrSession session, String name)
    {
        WorkspaceImpl workspace = (WorkspaceImpl)session.getWorkspace().getDelegate();
        try
        {
            workspace.createWorkspace(name);
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
    }

    public void clone(JcrSession src, JcrSession dest)
    {
        cleanWorkspace(dest);
        cloneWorkspace(src, dest);
    }

    private void cleanWorkspace(JcrSession session)
    {
        if (session.itemExists(getRootPath()))
        {
            JcrNode root = (JcrNode)session.getItem(getRootPath());
            root.remove();
            session.save();
        }

        session.save();
    }

    private void cloneWorkspace(JcrSession srcSession, JcrSession destSession)
    {
        if (true)
        {
            String root = getRootPath();
            destSession.getWorkspace().clone(srcSession.getWorkspace().getName(), root, root, true);
            return;
        }
        JcrNode root = srcSession.getRootNode();
        JcrNodeIterator nodes = root.getNodes();
        while (nodes.hasNext())
        {
            JcrNode node = nodes.nextNode();
            if (node.getName().equals("jcr:system") == false)
            {
                destSession.getWorkspace().clone(srcSession.getWorkspace().getName(),
                        "/" + node.getName(), "/" + node.getName(), true);
            }
        }
    }

    public static final String STATE_DEVELOPMENT = "development";
    public static final String STATE_STAGING = "staging";
    public static final String STATE_PRODUCTION = "production";

    private WorkspaceResolver workspaceResolver;
    
    public WorkspaceResolver getWorkspaceResolver()
    {
        if (workspaceResolver == null)
        {
            workspaceResolver = newWorkspaceResolver();
        }
        return workspaceResolver;
    }
    
    protected WorkspaceResolver newWorkspaceResolver()
    {
        return new DefaultWorkspaceResolver('^');
    }
    
    public String getWorkspaceNameForState(String workspaceName, String state)
    {
        String prefix = getWorkspaceResolver().getWorkspacePrefix(workspaceName);
        String id = getWorkspaceResolver().getWorkspaceId(workspaceName);
        
        return getWorkspaceResolver().getWorkspaceName(prefix, id, state);               
    }

    public void publish(String workspace, String targetState, SessionProvider sessionProvider)
    {
        String dest = getWorkspaceNameForState(workspace, targetState);

        if (workspace.equals(dest) == false)
        {

            List<String> workspaces = getVisibleWorkspaces();
            if (workspaces.contains(dest) == false)
            {
                createWorkspace(sessionProvider.getJcrSession(null), dest);
            }

            cleanWorkspace(BrixRequestCycle.Locator.getSession(dest));

            cloneWorkspace(BrixRequestCycle.Locator.getSession(workspace), BrixRequestCycle.Locator.getSession(dest));
        }
    }

    public String fromRealWebNodePath(String nodePath)
    {
        Path prefix = new Path(getWebPath());
        Path path = new Path(nodePath);

        if (path.equals(prefix))
        {
            path = new Path("/");
        }
        else if (path.isDescendantOf(prefix))
        {
            path = path.toRelative(prefix);
        }

        if (!path.isAbsolute())
        {
            path = new Path("/").append(path);
        }

        return path.toString();
    }

    public String toRealWebNodePath(String nodePath)
    {
        Path prefix = new Path(getWebPath());
        Path path = new Path(nodePath);

        if (path.isRoot())
        {
            path = new Path(".");
        }
        else if (path.isAbsolute())
        {
            path = path.toRelative(new Path("/"));
        }

        return prefix.append(path).toString();
    }

    private AuthorizationStrategy authorizationStrategy = null;

    public final AuthorizationStrategy getAuthorizationStrategy()
    {
        if (authorizationStrategy == null)
        {
            authorizationStrategy = newAuthorizationStrategy();
        }
        return authorizationStrategy;
    }

    public abstract AuthorizationStrategy newAuthorizationStrategy();

    public static final String ROOT_NODE_NAME = NS_PREFIX + "root";

    public static final String WEB_NODE_NAME = NS_PREFIX + "web";

    public String getRootPath()
    {
        return "/" + ROOT_NODE_NAME;
    }

    public String getWebPath()
    {
        return getRootPath() + "/" + WEB_NODE_NAME;
    }

    private void registerType(Workspace workspace, String typeName, boolean referenceable,
            boolean orderable) throws Exception
    {

        NodeTypeManagerImpl manager = (NodeTypeManagerImpl)workspace.getNodeTypeManager();

        if (manager.hasNodeType(typeName) == false)
        {

            String type = "[" + typeName + "] > nt:unstructured ";

            if (referenceable)
                type += ", mix:referenceable ";

            if (orderable)
                type += "orderable ";

            type += " mixin";

            manager.registerNodeTypes(new StringInputStream(type),
                    JackrabbitNodeTypeManager.TEXT_X_JCR_CND, true);
        }
    }

    public void initRepository(Session session)
    {
        try
        {
            Workspace w = session.getWorkspace();
            NamespaceRegistry nr = w.getNamespaceRegistry();

            try
            {
                nr.registerNamespace(Brix.NS, "http://dexter.ibg.com");
            }
            catch (Exception ignore)
            {

            }

            EventUtil.registerSaveEventListener(new JcrEventListener());

            registerType(w, BrixNode.JCR_TYPE_BRIX_NODE, true, true);

            // the following three have always brix:node mixin too
            registerType(w, FolderNodePlugin.TYPE, false, false);
            registerType(w, TilePageNodePlugin.TYPE, false, false);
            registerType(w, TileTemplateNodePlugin.TYPE, false, false);

            registerType(w, TileContainerNode.JCR_TYPE_BRIX_TILE, false, true);

            registerType(w, BrixNode.JCR_MIXIN_BRIX_HIDDEN, false, false);

        }
        catch (Exception e)
        {
            log.error("Couldn't init jackrabbit repository, make sure you"
                    + " have the jcr.repository.location config property set", e);
        }
    }

    public void initWorkspace(JcrSession session)
    {
        JcrNode root;
        if (session.itemExists(getRootPath()))
        {
            root = (JcrNode)session.getItem(getRootPath());
        }
        else
        {
            root = session.getRootNode().addNode(ROOT_NODE_NAME, "nt:folder");
        }
        if (!root.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE))
        {
            root.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
        }

        JcrNode web;
        if (root.hasNode(WEB_NODE_NAME))
        {
            web = root.getNode(WEB_NODE_NAME);
        }
        else
        {
            web = root.addNode(WEB_NODE_NAME, "nt:folder");
        }
        if (!web.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE))
        {
            web.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
        }
    }

    private final WrapperRegistry wrapperRegistry = new WrapperRegistry();

    public WrapperRegistry getWrapperRegistry()
    {
        return wrapperRegistry;
    }

    private List<Plugin> plugins = new CopyOnWriteArrayList<Plugin>();
    
    public void registerPlugin(Plugin plugin) 
    {
        plugins.add(plugin);
    }
    
    public Plugin getPlugin(String id) 
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Argument 'id' may not be null.");
        }
        for (Plugin p : plugins)
        {
            if (id.equals(p.getId()))
            {
                return p;               
            }
        }
        return null;
    }
    
    public Collection<Plugin> getPlugins() 
    {
        return Collections.unmodifiableList(plugins);        
    }
    
    private static final Logger log = LoggerFactory.getLogger(Brix.class);
}
