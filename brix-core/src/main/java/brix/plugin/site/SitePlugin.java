package brix.plugin.site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import brix.Brix;
import brix.BrixRequestCycle;
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
    private static final String ID = SitePlugin.class.getName();

    public String getId()
    {
        return ID;
    }

    public NavigationTreeNode newNavigationTreeNode(String workspaceName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        JcrSession session = BrixRequestCycle.Locator.getSession(workspaceName);
        return new SiteNavigationTreeNode((JcrNode)session.getItem(brix.getWebPath()));
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
        List<String> workspaces = brix.getAvailableWorkspacesFiltered("site", null, null);
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
}
