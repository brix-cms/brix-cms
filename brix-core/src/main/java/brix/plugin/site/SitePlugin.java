package brix.plugin.site;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Path;
import brix.Plugin;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.node.fallback.FallbackNodePlugin;
import brix.plugin.site.node.folder.FolderNodePlugin;
import brix.plugin.site.node.resource.ResourceNodePlugin;
import brix.web.admin.navigation.NavigationTreeNode;
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

    public NavigationTreeNode newNavigationTreeNode(Workspace workspace)
    {
        JcrSession session = BrixRequestCycle.Locator.getSession(workspace.getId());
        return new SiteNavigationTreeNode((JcrNode)session.getItem(getSiteRootPath()));
    }

    public SitePlugin(Brix brix)
    {
        this.brix = brix;
        registerNodePlugin(new FolderNodePlugin());
        registerNodePlugin(new ResourceNodePlugin());
    }

    private Map<String, SiteNodePlugin> nodePlugins = new HashMap<String, SiteNodePlugin>();

    public void registerNodePlugin(SiteNodePlugin plugin)
    {
        if (plugin == null)
        {
            throw new IllegalArgumentException("Argument 'plugin' cannot be null");
        }

        final String type = plugin.getNodeType();

        if (nodePlugins.containsKey(type))
        {
            throw new IllegalStateException("Node plugin of type: " + plugin.getNodeType() +
                " already registered: " + nodePlugins.get(type).getClass().getName());
        }

        nodePlugins.put(type, plugin);
    }

    public Collection<SiteNodePlugin> getNodePlugins()
    {
        return Collections.unmodifiableCollection(nodePlugins.values());
    }

    public SiteNodePlugin getNodePluginForNode(JcrNode node)
    {
        final String type = ((BrixNode)node).getNodeType();

        final SiteNodePlugin plugin = nodePlugins.get(type);
        if (plugin == null)
        {
            return fallbackNodePlugin;
        }
        return plugin;
    }

    private FallbackNodePlugin fallbackNodePlugin = new FallbackNodePlugin();

    public SiteNodePlugin getNodePluginForType(String type)
    {
        final SiteNodePlugin plugin = nodePlugins.get(type);
        if (plugin == null)
        {
            return fallbackNodePlugin;
        }
        return plugin;
    }

    public static SitePlugin get(Brix brix)
    {
        return (SitePlugin)brix.getPlugin(ID);
    }

    public static SitePlugin get()
    {
        return get(BrixRequestCycle.Locator.getBrix());
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return "Site - " + getWorkspaceName(workspace) + " - " + getWorkspaceState(workspace);
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

    private static final String WORKSPACE_TYPE = "brix:site";

    private static final String WORKSPACE_ATTRIBUTE_NAME = "brix:site-name";

    private static final String WORKSPACE_ATTRIBUTE_STATE = "brix:site-state";
    
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
    
    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        List<Workspace> workspaces = brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
        // TODO sort workspaces
        return workspaces;
    }

    public static final String WEB_NODE_NAME = Brix.NS_PREFIX + "web";

    public String getSiteRootPath()
    {
        return brix.getRootPath() + "/" + WEB_NODE_NAME;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {
        JcrNode root = (JcrNode)workspaceSession.getItem(brix.getRootPath());
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

}
