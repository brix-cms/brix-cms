package brix.plugin.site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import brix.Brix;
import brix.Path;
import brix.Plugin;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupCache;
import brix.plugin.site.admin.NodeManagerContainerPanel;
import brix.plugin.site.fallback.FallbackNodePlugin;
import brix.plugin.site.folder.FolderNodePlugin;
import brix.plugin.site.resource.ResourceNodePlugin;
import brix.web.tab.AbstractWorkspaceTab;
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

	public ITab newTab(final Workspace workspace)
	{
    	return new Tab(new Model<String>("Site"), workspace);
	}
	
	static class Tab extends AbstractWorkspaceTab
	{
		public Tab(IModel<String> title, Workspace workspace)
		{
			super(title, workspace);
		}

		@Override
		public Panel<?> newPanel(String panelId, IModel<Workspace> workspaceModel)
		{
			return new NodeManagerContainerPanel(panelId, workspaceModel.getObject().getId());
		}
	};
	
	public SitePlugin(Brix brix)
	{
		this.brix = brix;
		registerNodePlugin(new FolderNodePlugin(this));
		registerNodePlugin(new ResourceNodePlugin(this));
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
		return getNodePluginForType(((BrixNode) node).getNodeType());
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
		return (SitePlugin) brix.getPlugin(ID);
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

	private Comparator<String> stateComparator = null;

	public void setStateComparator(Comparator<String> stateComparator)
	{
		this.stateComparator = stateComparator;
	}

	public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
	{
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		List<Workspace> workspaces = new ArrayList<Workspace>(brix.getWorkspaceManager().getWorkspacesFiltered(
				attributes));

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

	public static final String WEB_NODE_NAME = Brix.NS_PREFIX + "web";

	public String getSiteRootPath()
	{
		return brix.getRootPath() + "/" + WEB_NODE_NAME;
	}

	public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
	{
		JcrNode root = (JcrNode) workspaceSession.getItem(brix.getRootPath());
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
			return ((JcrNode) baseNode.getSession().getItem(strPath));
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
	
	private NodeManagerContainerPanel findContainer(Component<?> component)
	{
		if (component instanceof NodeManagerContainerPanel)
		{
			return (NodeManagerContainerPanel) component;
		}
		else
		{
			return component.findParent(NodeManagerContainerPanel.class);
		}
	}
	
	public void selectNode(Component<?> component, BrixNode node)
	{
		selectNode(component, node, false);
	}
	
	public void selectNode(Component<?> component, BrixNode node, boolean refreshTree)
	{
		NodeManagerContainerPanel panel = findContainer(component);
		if (panel != null)
		{
			panel.selectNode(node);
			panel.updateTree();
		}
		else
		{
			throw new IllegalStateException("Can't call selectNode with component outside of the hierarchy.");
		}
	}
	
	public void refreshNavigationTree(Component<?> component)
	{
		NodeManagerContainerPanel panel = findContainer(component);
		if (panel != null)
		{
			panel.updateTree();
		}
		else
		{
			throw new IllegalStateException("Can't call refreshNaviagtionTree with component outside of the hierarchy.");
		}
	}
}
