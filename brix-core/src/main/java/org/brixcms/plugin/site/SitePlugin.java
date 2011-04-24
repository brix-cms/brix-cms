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

package org.brixcms.plugin.site;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.brixcms.Brix;
import org.brixcms.Path;
import org.brixcms.SessionAwarePlugin;
import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.wrapper.NodeWrapper;
import org.brixcms.jcr.base.BrixSession;
import org.brixcms.jcr.base.action.AbstractActionHandler;
import org.brixcms.jcr.base.event.AddNodeEvent;
import org.brixcms.jcr.base.event.Event;
import org.brixcms.jcr.base.event.EventsListener;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.jcr.wrapper.ResourceNode;
import org.brixcms.markup.MarkupCache;
import org.brixcms.plugin.site.admin.NodeManagerContainerPanel;
import org.brixcms.plugin.site.admin.NodeTreeContainer;
import org.brixcms.plugin.site.admin.convert.ConvertNodeTabFactory;
import org.brixcms.plugin.site.admin.nodetree.AudioNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.CssNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.ImageNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.OfficeDocumentNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.OfficePresentationNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.OfficeSpreadsheetNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.PageNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.TemplateNodeTreeRenderer;
import org.brixcms.plugin.site.admin.nodetree.VideoNodeTreeRenderer;
import org.brixcms.plugin.site.auth.SiteNodeAction;
import org.brixcms.plugin.site.auth.SiteNodeAction.Type;
import org.brixcms.plugin.site.fallback.FallbackNodePlugin;
import org.brixcms.plugin.site.folder.FolderNodePlugin;
import org.brixcms.plugin.site.page.AbstractContainer;
import org.brixcms.plugin.site.page.PageNode;
import org.brixcms.plugin.site.page.PageSiteNodePlugin;
import org.brixcms.plugin.site.page.TemplateNode;
import org.brixcms.plugin.site.page.TemplateSiteNodePlugin;
import org.brixcms.plugin.site.page.admin.MarkupEditorFactory;
import org.brixcms.plugin.site.page.admin.SimpleMarkupEditorFactory;
import org.brixcms.plugin.site.page.global.GlobalContainerNode;
import org.brixcms.plugin.site.page.global.GlobalTilesPanel;
import org.brixcms.plugin.site.page.global.GlobalVariablesPanel;
import org.brixcms.plugin.site.page.tile.TileContainerFacet;
import org.brixcms.plugin.site.resource.ResourceNodePlugin;
import org.brixcms.plugin.site.webdav.Rule;
import org.brixcms.plugin.site.webdav.RulesNode;
import org.brixcms.plugin.site.webdav.RulesPanel;
import org.brixcms.registry.ExtensionPointRegistry;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.JcrException;
import org.brixcms.workspace.Workspace;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SitePlugin implements SessionAwarePlugin {
// ------------------------------ FIELDS ------------------------------

    public static final String PREFIX = "site";

    public static final String WORKSPACE_ATTRIBUTE_STATE = "brix:site-state";
    private static final String ID = SitePlugin.class.getName();

    private static final String WORKSPACE_TYPE = "brix:site";

    private static final String WORKSPACE_ATTRIBUTE_NAME = "brix:site-name";

    private static final String WEB_NODE_NAME = Brix.NS_PREFIX + "web";

    private static final String SITE_NODE_NAME = Brix.NS_PREFIX + "site";

    private static final String GLOBAL_CONTAINER_NODE_NAME = Brix.NS_PREFIX + "globalContainer";

    private static final String WEBDAV_RULES_NODE_NAME = Brix.NS_PREFIX + "webDavRules";

    private final Brix brix;

    private FallbackNodePlugin fallbackNodePlugin = new FallbackNodePlugin();

    private Comparator<String> stateComparator = null;

    private MarkupCache markupCache = new MarkupCache();

    private WebDavEventListener webDavEventListener = new WebDavEventListener();

// -------------------------- STATIC METHODS --------------------------

    public static SitePlugin get() {
        return get(Brix.get());
    }

    public static SitePlugin get(Brix brix) {
        return (SitePlugin) brix.getPlugin(ID);
    }

// --------------------------- CONSTRUCTORS ---------------------------

    public SitePlugin(Brix brix) {
        this.brix = brix;
        registerNodePlugin(new FolderNodePlugin(this));
        registerNodePlugin(new ResourceNodePlugin(this));
        registerNodePlugin(new TemplateSiteNodePlugin(this));
        registerNodePlugin(new PageSiteNodePlugin(this));
        registerManageNodeTabFactory(new ConvertNodeTabFactory());

        // register default editor
        ExtensionPointRegistry registry = brix.getConfig().getRegistry();
        registry.register(MarkupEditorFactory.POINT, new SimpleMarkupEditorFactory());

        // register node types for tree renderer
        registry.register(NodeTreeRenderer.POINT, new PageNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new TemplateNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new CssNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new AudioNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new ImageNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new VideoNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new OfficeDocumentNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new OfficeSpreadsheetNodeTreeRenderer());
        registry.register(NodeTreeRenderer.POINT, new OfficePresentationNodeTreeRenderer());
    }

    public void registerNodePlugin(SiteNodePlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Argument 'plugin' cannot be null");
        }

        brix.getConfig().getRegistry().register(SiteNodePlugin.POINT, plugin);
    }

    public void registerManageNodeTabFactory(ManageNodeTabFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Argument 'factory' cannot be null");
        }
        brix.getConfig().getRegistry().register(ManageNodeTabFactory.POINT, factory);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public final Brix getBrix() {
        return brix;
    }

    public MarkupCache getMarkupCache() {
        return markupCache;
    }

    public void setStateComparator(Comparator<String> stateComparator) {
        this.stateComparator = stateComparator;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Plugin ---------------------

    public String getId() {
        return ID;
    }

    public List<IBrixTab> newTabs(final IModel<Workspace> workspaceModel) {
        IBrixTab tabs[] = new IBrixTab[]{new SiteTab(new ResourceModel("site", "Site"), workspaceModel),
                new GlobalTilesTab(new ResourceModel("tiles", "Tiles"), workspaceModel),
                new GlobalVariablesTab(new ResourceModel("variables", "Variables"), workspaceModel),
                new WebDAVRulesTab(new ResourceModel("webdav.rules", "WebDAV Rules"), workspaceModel)};
        return Arrays.asList(tabs);
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {
        JcrNode root = (JcrNode) workspaceSession.getItem(brix.getRootPath());
        JcrNode web = null;
        if (root.hasNode(WEB_NODE_NAME)) {
            web = root.getNode(WEB_NODE_NAME);
        } else if (isSiteWorkspace(workspace)) {
            web = root.addNode(WEB_NODE_NAME, "nt:folder");
        }

        if (web != null) {
            if (!web.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE)) {
                web.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
            }

            checkForSiteRoot(web);

            if (!web.hasNode(GLOBAL_CONTAINER_NODE_NAME)) {
                GlobalContainerNode.initialize(web.addNode(GLOBAL_CONTAINER_NODE_NAME, "nt:file"));
            }

            if (!web.hasNode(WEBDAV_RULES_NODE_NAME)) {
                RulesNode.initialize((BrixNode) web.addNode(WEBDAV_RULES_NODE_NAME,
                        "nt:unstructured"));
            }
        }
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        List<Workspace> workspaces = new ArrayList<Workspace>(brix.getWorkspaceManager()
                .getWorkspacesFiltered(attributes));

        Collections.sort(workspaces, new Comparator<Workspace>() {
            public int compare(Workspace o1, Workspace o2) {
                String n1 = getWorkspaceName(o1);
                String n2 = getWorkspaceName(o2);

                int r = n1.compareTo(n2);
                if (r == 0) {
                    String s1 = getWorkspaceState(o1);
                    String s2 = getWorkspaceState(o2);

                    if (s1 != null && s2 != null) {
                        if (stateComparator != null) {
                            return stateComparator.compare(s1, s2);
                        } else {
                            return s1.compareTo(s2);
                        }
                    } else {
                        return 0;
                    }
                } else {
                    return r;
                }
            }
        });

        return workspaces;
    }

    public boolean isPluginWorkspace(Workspace workspace) {
        return isSiteWorkspace(workspace);
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        String name = "Site - " + getWorkspaceName(workspace);
        String state = getWorkspaceState(workspace);
        if (!Strings.isEmpty(state)) {
            name = name + " - " + state;
        }
        return name;
    }

// --------------------- Interface SessionAwarePlugin ---------------------


    public void onWebDavSession(final BrixSession session) {
        session.addEventsListener(webDavEventListener);
        session.addActionHandler(new WebDavActionHandler(session));
    }

// -------------------------- OTHER METHODS --------------------------

    public boolean canAddNodeChild(BrixNode node, Context context) {
        if (!isNodeEditable(node)) {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_ADD_CHILD, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canDeleteNode(BrixNode node, Context context) {
        if (!isNodeEditable(node)) {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_DELETE, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canEditNode(BrixNode node, Context context) {
        if (!isNodeEditable(node)) {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_EDIT, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    private boolean isNodeEditable(BrixNode node) {
        if (node.isNodeType("mix:versionable") && !node.isCheckedOut()) {
            return false;
        }
        if (node.isLocked() && node.getLock().getLockToken() == null) {
            return false;
        }
        return true;
    }

    public boolean canRenameNode(BrixNode node, Context context) {
        if (!isNodeEditable(node)) {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_DELETE, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canViewNode(BrixNode node, Context context) {
        Action action = new SiteNodeAction(context, Type.NODE_VIEW, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canViewNodeChildren(BrixNode node, Context context) {
        Action action = new SiteNodeAction(context, Type.NODE_VIEW_CHILDREN, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    private void checkForSiteRoot(JcrNode webNode) {
        if (!webNode.hasNode(SITE_NODE_NAME)) {
            JcrNode site = webNode.addNode(SITE_NODE_NAME, "nt:folder");
            site.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);

            JcrNodeIterator nodes = webNode.getNodes();
            while (nodes.hasNext()) {
                BrixNode node = (BrixNode) nodes.nextNode();
                if (node.isSame(site) == false && node instanceof GlobalContainerNode == false) {
                    JcrSession session = webNode.getSession();
                    session.move(node.getPath(), site.getPath() + "/" + node.getName());
                }
            }
        } else {
            // make reference for brix:site to brix:web to prevent creating prototypes
            // without selecting brix:web
            JcrNode site = webNode.getNode(SITE_NODE_NAME);
            if (!site.hasProperty(Brix.NS_PREFIX + "web")) {
                site.setProperty(Brix.NS_PREFIX + "web", webNode);
            }
        }
    }

    public Workspace createSite(String name, String state) {
        Workspace workspace = brix.getWorkspaceManager().createWorkspace();
        workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        setWorkspaceName(workspace, name);
        setWorkspaceState(workspace, state);
        return workspace;
    }

    public void setWorkspaceName(Workspace workspace, String name) {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_NAME, name);
    }

    public void setWorkspaceState(Workspace workspace, String state) {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_STATE, state);
    }

    public Collection<String> getGlobalTileIDs(JcrSession session) {
        AbstractContainer globalContainer = getGlobalContainer(session);
        Set<String> result;
        if (globalContainer != null) {
            result = new HashSet<String>();
            for (BrixNode n : globalContainer.tiles().getTileNodes()) {
                String id = TileContainerFacet.getTileId(n);
                if (!Strings.isEmpty(id)) {
                    result.add(id);
                }
            }
        } else {
            result = Collections.emptySet();
        }
        return result;
    }

    public Collection<String> getGlobalVariableKeys(JcrSession session) {
        AbstractContainer globalContainer = getGlobalContainer(session);
        Collection<String> result;
        if (globalContainer != null) {
            result = globalContainer.getSavedVariableKeys();
        } else {
            result = Collections.emptyList();
        }
        return result;
    }

    public AbstractContainer getGlobalContainer(JcrSession session) {
        if (session.itemExists(getGlobalContainerPath())) {
            return (AbstractContainer) session.getItem(getGlobalContainerPath());
        } else {
            return null;
        }
    }

    private String getGlobalContainerPath() {
        return getWebRootPath() + "/" + GLOBAL_CONTAINER_NODE_NAME;
    }

    public String getWebRootPath() {
        return brix.getRootPath() + "/" + WEB_NODE_NAME;
    }

    public String getGlobalVariableValue(JcrSession session, String variableKey) {
        AbstractContainer globalContainer = getGlobalContainer(session);
        if (globalContainer != null) {
            return globalContainer.getVariableValue(variableKey, false);
        } else {
            return null;
        }
    }

    public SiteNodePlugin getNodePluginForNode(JcrNode node) {
        return getNodePluginForType(((BrixNode) node).getNodeType());
    }

    public SiteNodePlugin getNodePluginForType(String type) {
        for (SiteNodePlugin plugin : getNodePlugins()) {
            if (plugin.getNodeType().equals(type)) {
                return plugin;
            }
        }
        return fallbackNodePlugin;
    }

    public Collection<SiteNodePlugin> getNodePlugins() {
        return brix.getConfig().getRegistry().lookupCollection(SiteNodePlugin.POINT);
    }

    public BrixNode getSiteRootNode(String workspaceId) {
        JcrSession workspaceSession = brix.getCurrentSession(workspaceId);
        BrixNode root = (BrixNode) workspaceSession.getItem(getSiteRootPath());
        return root;
    }

    public String getSiteRootPath() {
        return getWebRootPath() + "/" + SITE_NODE_NAME;
    }

    public RulesNode getWebDavRules(JcrSession session) {
        if (session.itemExists(getWebDavRulesPath())) {
            return (RulesNode) session.getItem(getWebDavRulesPath());
        } else {
            return null;
        }
    }

    private String getWebDavRulesPath() {
        return getWebRootPath() + "/" + WEBDAV_RULES_NODE_NAME;
    }

    public String getWorkspaceName(Workspace workspace) {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_NAME);
    }

    public String getWorkspaceState(Workspace workspace) {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_STATE);
    }

    ;

    private void handleNewNode(String path, BrixNode node, JcrSession session, boolean save) {
        if (path.startsWith(getSiteRootPath()) == false) {
            return;
        }
        String relativePath = path.substring(getSiteRootPath().length());

        List<Rule> rules = getWebDavRules(session).getRules(true);
        for (Rule rule : rules) {
            if (rule.matches(relativePath)) {
                if (node == null) {
                    node = (BrixNode) session.getItem(path);
                }

                if (node instanceof ResourceNode == false) {
                    return;
                }

                AbstractContainer container = null;
                if (rule.getType() == Rule.Type.TEMPLATE) {
                    container = TemplateNode.initialize(node);
                } else if (rule.getType() == Rule.Type.PAGE) {
                    container = PageNode.initialize(node);
                }

                if (container != null) {
                    if (rule.getTemplateModel().getObject() != null) {
                        container.setTemplate(rule.getTemplateModel().getObject());
                    }
                    if (save) {
                        container.save();
                    }
                }

                return;
            }
        }
    }

    public boolean isSiteWorkspace(Workspace workspace) {
        return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
    }

    public JcrNode nodeForPath(BrixNode baseNode, Path path) {
        return nodeForPath(baseNode, path.toString());
    }

    public JcrNode nodeForPath(BrixNode baseNode, String path) {
        Path realPath = new Path(SitePlugin.get().toRealWebNodePath(path));

        if (realPath.isAbsolute() == false) {
            Path base = new Path(baseNode.getPath());
            if (!baseNode.isFolder()) {
                base = base.parent();
            }
            realPath = base.append(realPath);
        }

        String strPath = realPath.toString();
        if (baseNode.getSession().itemExists(strPath) == false) {
            return null;
        } else {
            return ((JcrNode) baseNode.getSession().getItem(strPath));
        }
    }

    public String toRealWebNodePath(String nodePath) {
        Path prefix = new Path(getSiteRootPath());
        Path path = new Path(nodePath);

        if (path.isRoot()) {
            path = new Path(".");
        } else if (path.isAbsolute()) {
            path = path.toRelative(new Path("/"));
        }

        return prefix.append(path).toString();
    }

    public String pathForNode(JcrNode node) {
        return SitePlugin.get().fromRealWebNodePath(node.getPath());
    }

    public String fromRealWebNodePath(String nodePath) {
        Path prefix = new Path(getSiteRootPath());
        Path path = new Path(nodePath);

        if (path.equals(prefix)) {
            path = new Path("/");
        } else if (path.isDescendantOf(prefix)) {
            path = path.toRelative(prefix);
        }

        if (!path.isAbsolute()) {
            path = new Path("/").append(path);
        }

        return path.toString();
    }

    public void refreshNavigationTree(Component component) {
        NodeTreeContainer panel = findContainer(component);
        if (panel != null) {
            panel.updateTree();
        } else {
            throw new IllegalStateException(
                    "Can't call refreshNaviagtionTree with component outside of the hierarchy.");
        }
    }

    public void selectNode(Component component, BrixNode node) {
        selectNode(component, node, false);
    }

    public void selectNode(Component component, BrixNode node, boolean refreshTree) {
        NodeTreeContainer panel = findContainer(component);
        if (panel != null) {
            panel.selectNode(node);
            panel.updateTree();
        } else {
            throw new IllegalStateException(
                    "Can't call selectNode with component outside of the hierarchy.");
        }
    }

    private NodeTreeContainer findContainer(Component component) {
        if (component instanceof NodeTreeContainer) {
            return (NodeTreeContainer) component;
        } else {
            return component.findParent(NodeTreeContainer.class);
        }
    }

    public boolean siteExists(String name, String state) {
        return getSiteWorkspace(name, state) != null;
    }

    public Workspace getSiteWorkspace(String name, String state) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        attributes.put(WORKSPACE_ATTRIBUTE_NAME, name);

        if (state != null) {
            attributes.put(WORKSPACE_ATTRIBUTE_STATE, state);
        }
        List<Workspace> res = brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
        return res.isEmpty() ? null : res.get(0);
    }

    private BrixNode wrapNode(Node node) {
        JcrSession session;
        try {
            session = brix.wrapSession(node.getSession());
            return (BrixNode) NodeWrapper.wrap(node, session);
        } catch (RepositoryException e) {
            throw new JcrException(e);
        }
    }

// -------------------------- INNER CLASSES --------------------------

    static class SiteTab extends AbstractWorkspaceTab {
        public SiteTab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 1000);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new NodeManagerContainerPanel(panelId, workspaceModel);
        }
    }

    ;

    static class GlobalTilesTab extends AbstractWorkspaceTab {
        public GlobalTilesTab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 999);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new GlobalTilesPanel(panelId, workspaceModel);
        }

        @Override
        public boolean isVisible() {
            JcrSession session = Brix.get().getCurrentSession(
                    getWorkspaceModel().getObject().getId());
            SitePlugin sp = SitePlugin.get();
            return sp.canEditNode(sp.getGlobalContainer(session), Context.ADMINISTRATION);
        }
    }

    ;

    static abstract class AuthorizedWorkspaceTab extends AbstractWorkspaceTab {
        public AuthorizedWorkspaceTab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel);
        }

        public AuthorizedWorkspaceTab(IModel<String> title, IModel<Workspace> workspaceModel,
                                      int priority) {
            super(title, workspaceModel, priority);
        }

        @Override
        public boolean isVisible() {
            JcrSession session = Brix.get().getCurrentSession(
                    getWorkspaceModel().getObject().getId());
            SitePlugin sp = SitePlugin.get();
            return sp.canEditNode(sp.getGlobalContainer(session), Context.ADMINISTRATION);
        }
    }

    static class GlobalVariablesTab extends AuthorizedWorkspaceTab {
        public GlobalVariablesTab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 998);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new GlobalVariablesPanel(panelId, workspaceModel);
        }
    }

    ;

    static class WebDAVRulesTab extends AuthorizedWorkspaceTab {
        public WebDAVRulesTab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 0);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new RulesPanel(panelId, workspaceModel);
        }
    }

    private class WebDavEventListener implements EventsListener {
        public void handleEventsBeforeSave(Session session, Item item, List<Event> events)
                throws RepositoryException {
            for (Event e : events) {
                if (e instanceof AddNodeEvent) {
                    AddNodeEvent event = (AddNodeEvent) e;

                    BrixNode node = wrapNode(event.getNewNode());
                    if (node instanceof ResourceNode) {
                        handleNewNode(node.getPath(), node, brix.wrapSession(session), false);
                    }
                }
            }
        }

        public void handleEventsAfterSave(Session session, Item item, List<Event> events)
                throws RepositoryException {
        }
    }

    private final class WebDavActionHandler extends AbstractActionHandler {
        private final BrixSession session;

        private WebDavActionHandler(BrixSession session) {
            this.session = session;
        }

        @Override
        public void afterWorkspaceMove(String srcAbsPath, String destAbsPath)
                throws RepositoryException {
            String n1 = new Path(srcAbsPath).getName().toLowerCase();

            // check if the item had extension before renaming. This is used to handle special case
            // when coda creates
            // "Untitled file" node that is later renamed to real file name
            if (n1.lastIndexOf('.') > n1.lastIndexOf('/')) {
                // it had, this is probably not a new file
                return;
            }

            handleNewNode(destAbsPath, null, brix.wrapSession(session), true);
        }
    }
}
