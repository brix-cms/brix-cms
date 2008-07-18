package brix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.protocol.http.WebApplication;

import brix.auth.Action;
import brix.auth.AuthorizationStrategy;
import brix.auth.ViewWorkspaceAction;
import brix.auth.Action.Context;
import brix.config.BrixConfig;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryInitializer;
import brix.jcr.SessionBehavior;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.SiteRootNode;
import brix.plugin.site.WebRootNode;
import brix.plugin.site.folder.FolderNode;
import brix.plugin.site.page.PageNode;
import brix.plugin.site.page.TemplateNode;
import brix.plugin.site.page.global.GlobalContainerNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.webdav.RulesNode;
import brix.registry.ExtensionPointRegistry;
import brix.web.nodepage.BrixNodePageUrlCodingStrategy;
import brix.web.nodepage.PageParametersAwareEnabler;
import brix.web.tile.pagetile.PageTile;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

/**
 * TODO doc
 * 
 * Before brix can be used {@link #initRepository()} method should be called.
 * 
 * 
 * @author igor.vaynberg
 * 
 */
public abstract class Brix
{
    public static final String NS = "brix";
    public static final String NS_PREFIX = NS + ":";

    private final BrixConfig config;

    private static MetaDataKey<Brix> APP_KEY = new MetaDataKey<Brix>()
    {
    };

    public Brix(BrixConfig config)
    {
        this.config = config;

        final ExtensionPointRegistry registry = config.getRegistry();

        registry.register(RepositoryInitializer.POINT, new BrixRepositoryInitializer());

        registry.register(JcrNodeWrapperFactory.POINT, SiteRootNode.FACTORY);
        registry.register(JcrNodeWrapperFactory.POINT, WebRootNode.FACTORY);
        registry.register(JcrNodeWrapperFactory.POINT, FolderNode.FACTORY);
        registry.register(JcrNodeWrapperFactory.POINT, GlobalContainerNode.FACTORY);

        registry.register(JcrNodeWrapperFactory.POINT, PageNode.FACTORY);
        registry.register(JcrNodeWrapperFactory.POINT, TemplateNode.FACTORY);
        
        registry.register(JcrNodeWrapperFactory.POINT, RulesNode.FACTORY);

        registry.register(Tile.POINT, new PageTile());

        registry.register(Plugin.POINT, new SitePlugin(this));
// registry.register(Plugin.POINT, new MenuPlugin(this));
// registry.register(Plugin.POINT, new SnapshotPlugin(this));
        // registry.register(Plugin.POINT, new PrototypePlugin(this));
        // registry.register(Plugin.POINT, new PublishingPlugin(this));


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
        Session session = config.getSessionFactory().getCurrentSession(workspace);
        return wrapSession(session);
    }

    public JcrSession wrapSession(Session session)
    {
        SessionBehavior behavior = new SessionBehavior(this);
        return JcrSession.Wrapper.wrap(session, behavior);
    }

    /**
     * Performs any {@link WebApplication} specific initialization
     * 
     * @param application
     */
    public void attachTo(WebApplication application)
    {
        if (application == null)
        {
            throw new IllegalArgumentException("Application cannot be null");
        }

        // store brix instance in applicaton's metadata so it can be retrieved easily later
        application.setMetaData(APP_KEY, this);

        /*
         * XXX we are coupling to nodepage plugin here instead of using the usual register mechanism
         * - we either need to make plugins application aware so they can install their own
         * listeners or have some brix-level registery
         */
        application.addPreComponentOnBeforeRenderListener(new PageParametersAwareEnabler());


        // allow brix to handle any url that wicket cant
        application.mount(new BrixNodePageUrlCodingStrategy());

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

    public final WorkspaceManager getWorkspaceManager()
    {
        return config.getWorkspaceManager();
    }

    public static final String WORKSPACE_ATTRIBUTE_TYPE = "brix:workspace-type";

    /*
     * public void publish(String workspace, String targetState, SessionProvider sessionProvider) {
     * String dest = getWorkspaceNameForState(workspace, targetState);
     * 
     * if (workspace.equals(dest) == false) { List<String> workspaces = getAvailableWorkspaces(); if
     * (workspaces.contains(dest) == false) { createWorkspace(sessionProvider.getJcrSession(null),
     * dest); }
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


    public void initRepository()
    {
        List<RepositoryInitializer> initializers = new ArrayList<RepositoryInitializer>();
        initializers.addAll(config.getRegistry().lookupCollection(RepositoryInitializer.POINT));
        initializers.addAll(config.getRegistry().lookupCollection(JcrNodeWrapperFactory.POINT));

        try
        {
            JcrSession s = getCurrentSession(null);
            for (RepositoryInitializer initializer : initializers)
            {
                initializer.initializeRepository(this, s);
            }
            s.save();
            s.logout();
        }
        catch (RepositoryException e)
        {
            throw new RuntimeException("Couldn't init jackrabbit repository", e);
        }

        for (Workspace w : getWorkspaceManager().getWorkspaces())
        {
            JcrSession s = getCurrentSession(w.getId());
            initWorkspace(w, s);
            s.logout();
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
        session.save();
    }

    public final Collection<Plugin> getPlugins()
    {
        return config.getRegistry().lookupCollection(Plugin.POINT);
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
}
