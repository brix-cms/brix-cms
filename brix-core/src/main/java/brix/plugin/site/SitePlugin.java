package brix.plugin.site;

import java.util.ArrayList;
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
import brix.web.nodepage.toolbar.WorkspaceListProvider;

public class SitePlugin implements Plugin, WorkspaceListProvider
{
    public static final String PREFIX = "site";
    private static final String ID = SitePlugin.class.getName();

    public String getId()
    {
        return ID;
    }

    public NavigationTreeNode newNavigationTreeNode(String workspaceName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        JcrSession session = BrixRequestCycle.Locator.getSession(workspaceName);
        return new SiteNavigationTreeNode((JcrNode)session.getItem(getSiteRootPath()));
    }

    public SitePlugin()
    {
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

    public List<Entry> getVisibleWorkspaces(String currentWorkspaceName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        List<String> workspaces = brix.getAvailableWorkspacesFiltered(PREFIX, null, null);
        List<Entry> res = new ArrayList<Entry>();
        for (String s : workspaces)
        {
            Entry e = new Entry();
            e.workspaceName = s;
            String id = brix.getWorkspaceResolver().getWorkspaceId(s);
            e.userVisibleName = "Site " +
                brix.getWorkspaceResolver().getUserVisibleWorkspaceName(id) + " " +
                brix.getWorkspaceResolver().getWorkspaceState(s);
            res.add(e);
        }
        return res;
    }
    
    public static final String WEB_NODE_NAME = Brix.NS_PREFIX + "web";
    
    public String getSiteRootPath()
    {
        return BrixRequestCycle.Locator.getBrix().getRootPath() + "/" + WEB_NODE_NAME;
    }

    public void initWorkspace(JcrSession workspaceSession)
    {
        JcrNode root = (JcrNode)workspaceSession.getItem(BrixRequestCycle.Locator.getBrix().getRootPath());
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
