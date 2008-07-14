package brix.plugin.site;

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

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import brix.Brix;
import brix.Path;
import brix.Plugin;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupCache;
import brix.plugin.site.admin.NodeManagerContainerPanel;
import brix.plugin.site.admin.convert.ConvertNodeTabFactory;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.auth.SiteNodeAction.Type;
import brix.plugin.site.fallback.FallbackNodePlugin;
import brix.plugin.site.folder.FolderNodePlugin;
import brix.plugin.site.page.AbstractContainer;
import brix.plugin.site.page.PageSiteNodePlugin;
import brix.plugin.site.page.TemplateSiteNodePlugin;
import brix.plugin.site.page.global.GlobalContainerNode;
import brix.plugin.site.page.global.GlobalTilesPanel;
import brix.plugin.site.page.global.GlobalVariablesPanel;
import brix.plugin.site.page.tile.TileContainerFacet;
import brix.plugin.site.resource.ResourceNodePlugin;
import brix.web.tab.AbstractWorkspaceTab;
import brix.web.tab.IBrixTab;
import brix.workspace.Workspace;

public class SitePlugin implements Plugin
{
    public static final String PREFIX = "site";
    private static final String ID = SitePlugin.class.getName();

    private final Brix brix;

    public String getId()
    {
        return ID;
    }

    public List<IBrixTab> newTabs(final IModel<Workspace> workspaceModel)
    {
        IBrixTab tabs[] = new IBrixTab[] { new SiteTab(new Model<String>("Site"), workspaceModel),
            new GlobalTilesTab(new Model<String>("Tiles"), workspaceModel),
            new GlobalVariablesTab(new Model<String>("Variables"), workspaceModel) };
        return Arrays.asList(tabs);
    }

    static class SiteTab extends AbstractWorkspaceTab
    {
        public SiteTab(IModel<String> title, IModel<Workspace> workspaceModel)
        {
            super(title, workspaceModel, 1000);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
        {
            return new NodeManagerContainerPanel(panelId, workspaceModel);
        }
    };

    static class GlobalTilesTab extends AbstractWorkspaceTab
    {
        public GlobalTilesTab(IModel<String> title, IModel<Workspace> workspaceModel)
        {
            super(title, workspaceModel, 999);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
        {
            return new GlobalTilesPanel(panelId, workspaceModel);
        }
    };

    static class GlobalVariablesTab extends AbstractWorkspaceTab
    {
        public GlobalVariablesTab(IModel<String> title, IModel<Workspace> workspaceModel)
        {
            super(title, workspaceModel, 998);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
        {
            return new GlobalVariablesPanel(panelId, workspaceModel);
        }
    };

    public SitePlugin(Brix brix)
    {
        this.brix = brix;
        registerNodePlugin(new FolderNodePlugin(this));
        registerNodePlugin(new ResourceNodePlugin(this));
        registerNodePlugin(new TemplateSiteNodePlugin(this));
        registerNodePlugin(new PageSiteNodePlugin(this));
        registerManageNodeTabFactory(new ConvertNodeTabFactory());
    }


    public final Brix getBrix()
    {
        return brix;
    }

    public void registerManageNodeTabFactory(ManageNodeTabFactory factory)
    {
        if (factory == null)
        {
            throw new IllegalArgumentException("Argument 'factory' cannot be null");
        }
        brix.getConfig().getRegistry().register(ManageNodeTabFactory.POINT, factory);
    }

    public void registerNodePlugin(SiteNodePlugin plugin)
    {
        if (plugin == null)
        {
            throw new IllegalArgumentException("Argument 'plugin' cannot be null");
        }

        brix.getConfig().getRegistry().register(SiteNodePlugin.POINT, plugin);
    }

    public Collection<SiteNodePlugin> getNodePlugins()
    {
        return brix.getConfig().getRegistry().lookupCollection(SiteNodePlugin.POINT);
    }

    public SiteNodePlugin getNodePluginForNode(JcrNode node)
    {
        return getNodePluginForType(((BrixNode)node).getNodeType());
    }

    private FallbackNodePlugin fallbackNodePlugin = new FallbackNodePlugin();

    public SiteNodePlugin getNodePluginForType(String type)
    {
        for (SiteNodePlugin plugin : getNodePlugins())
        {
            if (plugin.getNodeType().equals(type))
            {
                return plugin;
            }
        }
        return fallbackNodePlugin;
    }

    public static SitePlugin get(Brix brix)
    {
        return (SitePlugin)brix.getPlugin(ID);
    }

    public static SitePlugin get()
    {
        return get(Brix.get());
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        String name = "Site - " + getWorkspaceName(workspace);
        String state = getWorkspaceState(workspace);
        if (!Strings.isEmpty(state))
        {
            name = name + " - " + state;
        }
        return name;
    }

    public void setWorkspaceName(Workspace workspace, String name)
    {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_NAME, name);
    }

    public String getWorkspaceName(Workspace workspace)
    {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_NAME);
    }

    public void setWorkspaceState(Workspace workspace, String state)
    {
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_STATE, state);
    }

    public String getWorkspaceState(Workspace workspace)
    {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_STATE);
    }

    public boolean isSiteWorkspace(Workspace workspace)
    {
        return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
    }

    public boolean isPluginWorkspace(Workspace workspace)
    {
        return isSiteWorkspace(workspace);
    }

    private static final String WORKSPACE_TYPE = "brix:site";

    private static final String WORKSPACE_ATTRIBUTE_NAME = "brix:site-name";

    public static final String WORKSPACE_ATTRIBUTE_STATE = "brix:site-state";

    public Workspace createSite(String name, String state)
    {
        Workspace workspace = brix.getWorkspaceManager().createWorkspace();
        workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        setWorkspaceName(workspace, name);
        setWorkspaceState(workspace, state);
        return workspace;
    }

    public Workspace getSiteWorkspace(String name, String state)
    {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        attributes.put(WORKSPACE_ATTRIBUTE_NAME, name);
        attributes.put(WORKSPACE_ATTRIBUTE_STATE, state);
        List<Workspace> res = brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
        return res.isEmpty() ? null : res.get(0);
    }

    public boolean siteExists(String name, String state)
    {
        return getSiteWorkspace(name, state) != null;
    }

    private Comparator<String> stateComparator = null;

    public void setStateComparator(Comparator<String> stateComparator)
    {
        this.stateComparator = stateComparator;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        List<Workspace> workspaces = new ArrayList<Workspace>(brix.getWorkspaceManager()
            .getWorkspacesFiltered(attributes));

        Collections.sort(workspaces, new Comparator<Workspace>()
        {
            public int compare(Workspace o1, Workspace o2)
            {
                String n1 = getWorkspaceName(o1);
                String n2 = getWorkspaceName(o2);

                int r = n1.compareTo(n2);
                if (r == 0)
                {
                    String s1 = getWorkspaceState(o1);
                    String s2 = getWorkspaceState(o2);

                    if (s1 != null && s2 != null)
                    {
                        if (stateComparator != null)
                        {
                            return stateComparator.compare(s1, s2);
                        }
                        else
                        {
                            return s1.compareTo(s2);
                        }
                    }
                    else
                    {
                        return 0;
                    }
                }
                else
                {
                    return r;
                }
            }
        });

        return workspaces;
    }

    private static final String WEB_NODE_NAME = Brix.NS_PREFIX + "web";
    
    private static final String SITE_NODE_NAME = Brix.NS_PREFIX + "site"; 

    private static final String GLOBAL_CONTAINER_NODE_NAME = Brix.NS_PREFIX + "globalContainer";

    private String getGlobalContainerPath()
    {
        return getWebRootPath() + "/" + GLOBAL_CONTAINER_NODE_NAME;
    }

    public String getWebRootPath()
    {
    	return brix.getRootPath() + "/" + WEB_NODE_NAME;	
    }

    public String getSiteRootPath()
    {
        return getWebRootPath() + "/" + SITE_NODE_NAME;
    }

    public BrixNode getSiteRootNode(String workspaceId)
    {
        JcrSession workspaceSession = brix.getCurrentSession(workspaceId);
        BrixNode root = (BrixNode)workspaceSession.getItem(getSiteRootPath());
        return root;
    }
    
    private void checkForSiteRoot(JcrNode webNode)
    {
    	if (!webNode.hasNode(SITE_NODE_NAME))
    	{
    		JcrNode site = webNode.addNode(SITE_NODE_NAME, "nt:folder");
    		site.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
    		
    		JcrNodeIterator nodes = webNode.getNodes();
    		while (nodes.hasNext())
    		{
    			BrixNode node = (BrixNode) nodes.nextNode();
    			if (node.isSame(site) == false && node instanceof GlobalContainerNode == false)
    			{
    				JcrSession session = webNode.getSession();
    				session.move(node.getPath(), site.getPath() + "/" + node.getName());
    			}
    		}
    	}
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {
        JcrNode root = (JcrNode)workspaceSession.getItem(brix.getRootPath());
        JcrNode web = null;
        if (root.hasNode(WEB_NODE_NAME))
        {
            web = root.getNode(WEB_NODE_NAME);
        }
        else if (isSiteWorkspace(workspace))
        {
            web = root.addNode(WEB_NODE_NAME, "nt:folder");
        }

        if (web != null)
        {
            if (!web.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE))
            {
                web.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
            }
            
            checkForSiteRoot(web);
            
            if (!web.hasNode(GLOBAL_CONTAINER_NODE_NAME))
            {
                GlobalContainerNode.initialize(web.addNode(GLOBAL_CONTAINER_NODE_NAME, "nt:file"));
            }
        }
    }

    public String fromRealWebNodePath(String nodePath)
    {
        Path prefix = new Path(getSiteRootPath());
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
        Path prefix = new Path(getSiteRootPath());
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

    public JcrNode nodeForPath(BrixNode baseNode, Path path)
    {
        return nodeForPath(baseNode, path.toString());
    }

    public JcrNode nodeForPath(BrixNode baseNode, String path)
    {
        Path realPath = new Path(SitePlugin.get().toRealWebNodePath(path));

        if (realPath.isAbsolute() == false)
        {
            Path base = new Path(baseNode.getPath());
            if (!baseNode.isFolder())
            {
                base = base.parent();
            }
            realPath = base.append(realPath);
        }

        String strPath = realPath.toString();
        if (baseNode.getSession().itemExists(strPath) == false)
        {
            return null;
        }
        else
        {
            return ((JcrNode)baseNode.getSession().getItem(strPath));
        }
    }

    public String pathForNode(JcrNode node)
    {
        return SitePlugin.get().fromRealWebNodePath(node.getPath());
    }

    public MarkupCache getMarkupCache()
    {
        return markupCache;
    }

    private MarkupCache markupCache = new MarkupCache();

    private NodeManagerContainerPanel findContainer(Component component)
    {
        if (component instanceof NodeManagerContainerPanel)
        {
            return (NodeManagerContainerPanel)component;
        }
        else
        {
            return component.findParent(NodeManagerContainerPanel.class);
        }
    }

    public void selectNode(Component component, BrixNode node)
    {
        selectNode(component, node, false);
    }

    public void selectNode(Component component, BrixNode node, boolean refreshTree)
    {
        NodeManagerContainerPanel panel = findContainer(component);
        if (panel != null)
        {
            panel.selectNode(node);
            panel.updateTree();
        }
        else
        {
            throw new IllegalStateException(
                "Can't call selectNode with component outside of the hierarchy.");
        }
    }

    public void refreshNavigationTree(Component component)
    {
        NodeManagerContainerPanel panel = findContainer(component);
        if (panel != null)
        {
            panel.updateTree();
        }
        else
        {
            throw new IllegalStateException(
                "Can't call refreshNaviagtionTree with component outside of the hierarchy.");
        }
    }

    public AbstractContainer getGlobalContainer(JcrSession session)
    {
        if (session.itemExists(getGlobalContainerPath()))
        {
            return (AbstractContainer)session.getItem(getGlobalContainerPath());
        }
        else
        {
            return null;
        }
    }

    public Collection<String> getGlobalVariableKeys(JcrSession session)
    {
        AbstractContainer globalContainer = getGlobalContainer(session);
        Collection<String> result;
        if (globalContainer != null)
        {
            result = globalContainer.getSavedVariableKeys();
        }
        else
        {
            result = Collections.emptyList();
        }
        return result;
    }

    public String getGlobalVariableValue(JcrSession session, String variableKey)
    {
        AbstractContainer globalContainer = getGlobalContainer(session);
        if (globalContainer != null)
        {
            return globalContainer.getVariableValue(variableKey, false);
        }
        else
        {
            return null;
        }
    }

    public Collection<String> getGlobalTileIDs(JcrSession session)
    {
        AbstractContainer globalContainer = getGlobalContainer(session);
        Set<String> result;
        if (globalContainer != null)
        {
            result = new HashSet<String>();
            for (BrixNode n : globalContainer.tiles().getTileNodes())
            {
                String id = TileContainerFacet.getTileId(n);
                if (!Strings.isEmpty(id))
                {
                    result.add(id);
                }
            }
        }
        else
        {
            result = Collections.emptySet();
        }
        return result;
    }

    public boolean canViewNode(BrixNode node, Context context)
    {
        Action action = new SiteNodeAction(context, Type.NODE_VIEW, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canViewNodeChildren(BrixNode node, Context context)
    {
        Action action = new SiteNodeAction(context, Type.NODE_VIEW_CHILDREN, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }


    private boolean isNodeEditable(BrixNode node)
    {
        if (node.isNodeType("mix:versionable") && !node.isCheckedOut())
        {
            return false;
        }
        if (node.isLocked() && node.getLock().getLockToken() == null)
        {
            return false;
        }
        return true;
    }

    public boolean canEditNode(BrixNode node, Context context)
    {
        if (!isNodeEditable(node))
        {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_EDIT, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canAddNodeChild(BrixNode node, Context context)
    {
        if (!isNodeEditable(node))
        {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_ADD_CHILD, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canDeleteNode(BrixNode node, Context context)
    {
        if (!isNodeEditable(node))
        {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_DELETE, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

    public boolean canRenameNode(BrixNode node, Context context)
    {
        if (!isNodeEditable(node))
        {
            return false;
        }
        Action action = new SiteNodeAction(context, Type.NODE_DELETE, node);
        return brix.getAuthorizationStrategy().isActionAuthorized(action);
    }

}

