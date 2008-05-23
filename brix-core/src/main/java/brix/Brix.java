package brix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.api.JackrabbitNodeTypeManager;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brix.auth.Action;
import brix.auth.AuthorizationStrategy;
import brix.auth.ViewWorkspaceAction;
import brix.auth.Action.Context;
import brix.config.BrixConfig;
import brix.jcr.JcrEventListener;
import brix.jcr.JcrSessionFactory;
import brix.jcr.SessionBehavior;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.event.EventUtil;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.WrapperRegistry;
import brix.plugin.menu.MenuPlugin;
import brix.plugin.publishing.PublishingPlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.node.folder.FolderNode;
import brix.plugin.site.node.folder.FolderNodePlugin;
import brix.plugin.site.node.tilepage.TileContainerNode;
import brix.plugin.site.node.tilepage.TilePageNodePlugin;
import brix.plugin.site.node.tilepage.TileTemplateNodePlugin;
import brix.plugin.snapshot.SnapshotPlugin;
import brix.plugin.template.TemplatePlugin;
import brix.plugin.webdavurl.WebdavUrlPlugin;
import brix.registry.PointRegistry;
import brix.util.StringInputStream;
import brix.web.nodepage.PageParametersAwareEnabler;
import brix.workspace.WorkspaceManager;

public abstract class Brix
{
    private static final Logger logger = LoggerFactory.getLogger(Brix.class);

    public static final String NS = "brix";
    public static final String NS_PREFIX = NS + ":";

    private final JcrSessionFactory sessionFactory;

    private final BrixConfig config;

    private static MetaDataKey<Brix> APP_KEY = new MetaDataKey<Brix>()
    {
    };

    public Brix(BrixConfig config, JcrSessionFactory sessionFactory)
    {
        this.config = config;
        this.sessionFactory = sessionFactory;

        wrapperRegistry.registerWrapper(FolderNode.class);

        final PointRegistry registry = config.getRegistry();
        registry.register(Plugin.POINT, new SitePlugin(this));
        registry.register(Plugin.POINT, new MenuPlugin());
        registry.register(Plugin.POINT, new SnapshotPlugin());
        registry.register(Plugin.POINT, new TemplatePlugin());
        registry.register(Plugin.POINT, new PublishingPlugin());
        registry.register(Plugin.POINT, new WebdavUrlPlugin());
    }

    public static Brix get(Application application)
    {
        if (application == null)
        {
            throw new IllegalArgumentException("application cannot be null");
        }
        Brix brix = application.getMetaData(APP_KEY);
        if (brix == null)
        {
            throw new IllegalStateException(
                "Could not find instance of Brix associated with application: " +
                    application.getApplicationKey() +
                    ". Make sure Brix.attachTo(this) was called in application's init() method");
        }
        return brix;
    }

    public static Brix get()
    {
        Application application = Application.get();
        if (application == null)
        {
            throw new IllegalStateException(
                "Could not find Application threadlocal; this method can only be called within a Wicket request");
        }
        return get(application);
    }

    public final BrixConfig getConfig()
    {
        return config;
    }

    public JcrSession getCurrentSession(String workspace)
    {

        SessionBehavior behavior = new SessionBehavior();
        behavior.setWrapperRegistry(wrapperRegistry);
        Session session = sessionFactory.getCurrentSession(workspace);

        return JcrSession.Wrapper.wrap(session, behavior);
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

        application.setMetaData(APP_KEY, this);
    }

    /**
     * @deprecated should forward to workspace manager?
     * @param session
     * @param name
     */
    protected void createWorkspace(JcrSession session, String name)
    {
        // TODO: Decouple this from BRIX
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
        String root = getRootPath();
        destSession.getWorkspace().clone(srcSession.getWorkspace().getName(), root, root, true);
    }

    private WorkspaceManager workspaceManager;

    protected abstract WorkspaceManager newWorkspaceManager();

    public WorkspaceManager getWorkspaceManager()
    {
        if (workspaceManager == null)
        {
            workspaceManager = newWorkspaceManager();
        }
        return workspaceManager;
    }

    public static final String WORKSPACE_ATTRIBUTE_TYPE = "brix:workspace-type";

    /*
     * public void publish(String workspace, String targetState, SessionProvider sessionProvider) {
     * String dest = getWorkspaceNameForState(workspace, targetState);
     * 
     * if (workspace.equals(dest) == false) { List<String> workspaces = getAvailableWorkspaces();
     * if (workspaces.contains(dest) == false) {
     * createWorkspace(sessionProvider.getJcrSession(null), dest); }
     * 
     * cleanWorkspace(BrixRequestCycle.Locator.getSession(dest));
     * 
     * cloneWorkspace(BrixRequestCycle.Locator.getSession(workspace), BrixRequestCycle.Locator
     * .getSession(dest)); } }
     */

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

    public String getRootPath()
    {
        return "/" + ROOT_NODE_NAME;
    }

    protected void registerType(Workspace workspace, String typeName, boolean referenceable,
            boolean orderable) throws Exception
    {

        JackrabbitNodeTypeManager manager = (JackrabbitNodeTypeManager)workspace
            .getNodeTypeManager();

        if (manager.hasNodeType(typeName) == false)
        {
            logger.info("Registering node type: {} in workspace {}", typeName, workspace.getName());

            String type = "[" + typeName + "] > nt:unstructured ";

            if (referenceable)
                type += ", mix:referenceable ";

            if (orderable)
                type += "orderable ";

            type += " mixin";

            manager.registerNodeTypes(new StringInputStream(type),
                JackrabbitNodeTypeManager.TEXT_X_JCR_CND);
        }
        else
        {
            logger.info("Type: {} already registered in workspace {}", typeName, workspace
                .getName());
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
                nr.registerNamespace(Brix.NS, "http://brix-cms.googlecode.com");
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

    public void initWorkspace(brix.workspace.Workspace workspace, JcrSession session)
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

        for (Plugin p : getPlugins())
        {
            p.initWorkspace(workspace, session);
        }
    }

    public final Collection<Plugin> getPlugins()
    {
        return config.getRegistry().lookup(Plugin.POINT);
    }

    private final WrapperRegistry wrapperRegistry = new WrapperRegistry();

    public final WrapperRegistry getWrapperRegistry()
    {
        // we reference this field directly, so getter has to be final
        return wrapperRegistry;
    }

    public Plugin getPlugin(String id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("Argument 'id' may not be null.");
        }


        for (Plugin p : getPlugins())
        {
            if (id.equals(p.getId()))
            {
                return p;
            }
        }
        return null;
    }


    public List<brix.workspace.Workspace> filterVisibleWorkspaces(
            List<brix.workspace.Workspace> workspaces, Context context)
    {
        if (workspaces == null)
        {
            return Collections.emptyList();
        }
        else
        {
            List<brix.workspace.Workspace> result = new ArrayList<brix.workspace.Workspace>(
                workspaces.size());
            for (brix.workspace.Workspace w : workspaces)
            {
                Action action = new ViewWorkspaceAction(context, w);
                if (getAuthorizationStrategy().isActionAuthorized(action))
                {
                    result.add(w);
                }
            }

            return result;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(Brix.class);
}
