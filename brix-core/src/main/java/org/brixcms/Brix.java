/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms;

import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.IPageRequestHandler;
import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.auth.AuthorizationStrategy;
import org.brixcms.auth.ViewWorkspaceAction;
import org.brixcms.config.BrixConfig;
import org.brixcms.exception.BrixException;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.RepositoryInitializer;
import org.brixcms.jcr.SessionBehavior;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.SiteRootNode;
import org.brixcms.plugin.site.WebRootNode;
import org.brixcms.plugin.site.folder.FolderNode;
import org.brixcms.plugin.site.page.PageNode;
import org.brixcms.plugin.site.page.TemplateNode;
import org.brixcms.plugin.site.page.global.GlobalContainerNode;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.plugin.site.webdav.RulesNode;
import org.brixcms.registry.ExtensionPointRegistry;
import org.brixcms.web.BrixExtensionStringResourceLoader;
import org.brixcms.web.BrixRequestMapper;
import org.brixcms.web.nodepage.BrixNodeRequestHandler;
import org.brixcms.web.nodepage.BrixNodeWebPage;
import org.brixcms.web.nodepage.BrixPageParameters;
import org.brixcms.web.nodepage.ForbiddenPage;
import org.brixcms.web.nodepage.PageParametersAwareEnabler;
import org.brixcms.web.tile.pagetile.PageTile;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceManager;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * TODO doc
 * <p/>
 * Before brix can be used {@link #initRepository()} method should be called.
 *
 * @author igor.vaynberg
 */
public abstract class Brix {
    public static final String NS = "brix";
    public static final String NS_PREFIX = NS + ":";

    public static final String WORKSPACE_ATTRIBUTE_TYPE = "brix:workspace-type";

    public static final String ROOT_NODE_NAME = NS_PREFIX + "root";

    private static MetaDataKey<Brix> APP_KEY = new MetaDataKey<Brix>() {
    };

    private final BrixConfig config;

    /*
      * public void publish(String workspace, String targetState, SessionProvider
      * sessionProvider) { String dest = getWorkspaceNameForState(workspace,
      * targetState);
      *
      * if (workspace.equals(dest) == false) { List<String> workspaces =
      * getAvailableWorkspaces(); if (workspaces.contains(dest) == false) {
      * createWorkspace(sessionProvider.getJcrSession(null), dest); }
      *
      * cleanWorkspace(BrixRequestCycle.Locator.getSession(dest));
      *
      * cloneWorkspace(BrixRequestCycle.Locator.getSession(workspace),
      * BrixRequestCycle.Locator .getSession(dest)); } }
      */

    private AuthorizationStrategy authorizationStrategy = null;

    public static Brix get() {
        Application application = Application.get();
        if (application == null) {
            throw new IllegalStateException(
                    "Could not find Application threadlocal; this method can only be called within a Wicket request");
        }
        return get(application);
    }

    public static Brix get(Application application) {
        if (application == null) {
            throw new IllegalArgumentException("application cannot be null");
        }
        Brix brix = application.getMetaData(APP_KEY);
        if (brix == null) {
            throw new IllegalStateException(
                    "Could not find instance of Brix associated with application: "
                            + application.getApplicationKey()
                            + ". Make sure Brix.attachTo(this) was called in application's init() method");
        }
        return brix;
    }

    /**
     * Constructs a URL to the current page. This method can only be called within an active wicket request because it
     * relies on the {@link org.apache.wicket.RequestCycle} threadlocal.
     *
     * @return url to the current brix page
     * @throws BrixException if the current request was not for a brix page
     */
    public static String urlForCurrentPage() {
        return urlForCurrentPage(new BrixPageParameters());
    }

    /**
     * Constructs a URL to the current page. This method can only be called within an active wicket request because it
     * relies on the {@link RequestCycle} threadlocal.
     *
     * @param params parameters to be encoded into the url
     * @return url to the current brix page
     * @throws BrixException if the current request was not for a brix page
     */
    public static String urlForCurrentPage(BrixPageParameters params) {
        IRequestHandler target = new BrixNodeRequestHandler(getCurrentPage(), params);
        String url = RequestCycle.get().urlFor(target).toString();
        target.detach(RequestCycle.get());
        return url;
    }

    /**
     * Returns current brix page being processed. Must only be called within a wicket request.
     *
     * @return brix page
     * @throws BrixException if current request was not to a brix page
     */
    private static BrixNodeWebPage getCurrentPage() {
        IRequestHandler target = RequestCycle.get().getActiveRequestHandler();
        BrixNodeWebPage page = null;
        if (target != null && target instanceof IPageRequestHandler) {
            IRequestablePage p = ((IPageRequestHandler) target).getPage();
            if (p instanceof BrixNodeWebPage) {
                page = (BrixNodeWebPage) p;
            }
        }
        if (page == null) {
            throw new BrixException(
                    "Couldn't obtain the BrixNodeWebPage instance from RequestTarget.");
        }
        return page;
    }

    public Brix(BrixConfig config) {
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

    public final AuthorizationStrategy getAuthorizationStrategy() {
        if (authorizationStrategy == null) {
            authorizationStrategy = newAuthorizationStrategy();
        }
        return authorizationStrategy;
    }

    public abstract AuthorizationStrategy newAuthorizationStrategy();

    public final BrixConfig getConfig() {
        return config;
    }

    /**
     * Performs any {@link WebApplication} specific initialization
     *
     * @param application
     */
    public void attachTo(WebApplication application) {
        if (application == null) {
            throw new IllegalArgumentException("Application cannot be null");
        }

        // store brix instance in applicaton's metadata so it can be retrieved
        // easily later
        application.setMetaData(APP_KEY, this);

        /*
           * XXX we are coupling to nodepage plugin here instead of using the
           * usual register mechanism - we either need to make plugins application
           * aware so they can install their own listeners or have some brix-level
           * registery
           */
        application.getComponentPreOnBeforeRenderListeners().add(new PageParametersAwareEnabler());


        // allow brix to handle any url that wicket cant
        application.getRootRequestMapperAsCompound().add(new BrixRequestMapper(this));
        // application.mount(new BrixNodePageUrlMapper());

        // register a string resource loader that allows any object that acts as
        // an extension supply its own resource bundle for the UI
        BrixExtensionStringResourceLoader loader = new BrixExtensionStringResourceLoader();
        application.getResourceSettings().getStringResourceLoaders().add(loader);
        config.getRegistry().register(loader, true);
    }

    public void clone(JcrSession src, JcrSession dest) {
        cleanWorkspace(dest);
        cloneWorkspace(src, dest);
    }

    private void cleanWorkspace(JcrSession session) {
        if (session.itemExists(getRootPath())) {
            JcrNode root = (JcrNode) session.getItem(getRootPath());
            root.remove();
            session.save();
        }

        session.save();
    }

    public String getRootPath() {
        return "/" + ROOT_NODE_NAME;
    }

    private void cloneWorkspace(JcrSession srcSession, JcrSession destSession) {
        String root = getRootPath();
        destSession.getWorkspace().clone(srcSession.getWorkspace().getName(), root, root, true);
    }

    /**
     * @param session
     * @param name
     * @deprecated should forward to workspace manager?
     */
    protected void createWorkspace(JcrSession session, String name) {
        session.getWorkspace().createWorkspace(name);
    }

    public List<org.brixcms.workspace.Workspace> filterVisibleWorkspaces(
            List<org.brixcms.workspace.Workspace> workspaces, Context context) {
        if (workspaces == null) {
            return Collections.emptyList();
        } else {
            List<org.brixcms.workspace.Workspace> result = new ArrayList<org.brixcms.workspace.Workspace>(
                    workspaces.size());
            for (org.brixcms.workspace.Workspace w : workspaces) {
                Action action = new ViewWorkspaceAction(context, w);
                if (getAuthorizationStrategy().isActionAuthorized(action)) {
                    result.add(w);
                }
            }

            return result;
        }
    }

    public RestartResponseException getForbiddenException() {
        return new RestartResponseException(ForbiddenPage.class);
    }

    public Plugin getPlugin(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Argument 'id' may not be null.");
        }


        for (Plugin p : getPlugins()) {
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    public final WorkspaceManager getWorkspaceManager() {
        return config.getWorkspaceManager();
    }

    public void initRepository() {
        List<RepositoryInitializer> initializers = new ArrayList<RepositoryInitializer>();
        initializers.addAll(config.getRegistry().lookupCollection(RepositoryInitializer.POINT));
        initializers.addAll(config.getRegistry().lookupCollection(JcrNodeWrapperFactory.POINT));

        try {
            JcrSession s = getCurrentSession(null);
            for (RepositoryInitializer initializer : initializers) {
                initializer.initializeRepository(this, s);
            }
            s.save();
            s.logout();
        }
        catch (RepositoryException e) {
            throw new RuntimeException("Couldn't initialize repository", e);
        }

        for (Workspace w : getWorkspaceManager().getWorkspaces()) {
            JcrSession s = getCurrentSession(w.getId());
            initWorkspace(w, s);
            s.logout();
        }
    }

    public JcrSession getCurrentSession(String workspace) {
        Session session = config.getSessionFactory().getCurrentSession(workspace);
        return wrapSession(session);
    }

    public JcrSession wrapSession(Session session) {
        SessionBehavior behavior = new SessionBehavior(this);
        return JcrSession.Wrapper.wrap(session, behavior);
    }

    public void initWorkspace(org.brixcms.workspace.Workspace workspace, JcrSession session) {
        JcrNode root;
        if (session.itemExists(getRootPath())) {
            root = (JcrNode) session.getItem(getRootPath());
        } else {
            root = session.getRootNode().addNode(ROOT_NODE_NAME, "nt:folder");
        }
        if (!root.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE)) {
            root.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
        }

        for (Plugin p : getPlugins()) {
            p.initWorkspace(workspace, session);
        }
        session.save();
    }

    public final Collection<Plugin> getPlugins() {
        return config.getRegistry().lookupCollection(Plugin.POINT);
    }
}
