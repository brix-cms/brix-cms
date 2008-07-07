package brix.plugin.prototype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;

import brix.Brix;
import brix.Path;
import brix.Plugin;
import brix.jcr.JcrUtil;
import brix.jcr.JcrUtil.ParentLimiter;
import brix.jcr.JcrUtil.TargetRootNodeProvider;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.page.global.GlobalContainerNode;
import brix.web.tab.AbstractWorkspaceTab;
import brix.web.tab.IBrixTab;
import brix.workspace.Workspace;

public class PrototypePlugin implements Plugin
{

	private static final String ID = PrototypePlugin.class.getName();

	private final Brix brix;

	public PrototypePlugin(Brix brix)
	{
		this.brix = brix;
	}

	public String getId()
	{
		return ID;
	}

	public static PrototypePlugin get(Brix brix)
	{
		return (PrototypePlugin) brix.getPlugin(ID);
	}

	public static PrototypePlugin get()
	{
		return get(Brix.get());
	}

	private static final String WORKSPACE_TYPE = "brix:prototype";

	private static final String WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME = "brix:prototype-name";

	public boolean isPrototypeWorkspace(Workspace workspace)
	{
		return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
	}

	public void setPrototypeName(Workspace workspace, String name)
	{
		workspace.setAttribute(WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME, name);
	}

	public String getPrototypeName(Workspace workspace)
	{
		return workspace.getAttribute(WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME);
	}

	public List<Workspace> getPrototypes()
	{
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		return brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
	}

	public boolean prototypeExists(String protypeName)
	{
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		attributes.put(WORKSPACE_ATTRIBUTE_PROTOTYPE_NAME, protypeName);
		return !brix.getWorkspaceManager().getWorkspacesFiltered(attributes).isEmpty();
	}

	public void createPrototype(Workspace originalWorkspace, String prototypeName)
	{
		Workspace workspace = brix.getWorkspaceManager().createWorkspace();
		workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		setPrototypeName(workspace, prototypeName);

		JcrSession originalSession = brix.getCurrentSession(originalWorkspace.getId());
		JcrSession destSession = brix.getCurrentSession(workspace.getId());
		brix.clone(originalSession, destSession);
	}

	public void createPrototype(List<JcrNode> nodes, String prototypeName)
	{
		if (nodes.isEmpty())
		{
			throw new IllegalStateException("Node list can not be empty.");
		}
		Workspace workspace = brix.getWorkspaceManager().createWorkspace();
		workspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		setPrototypeName(workspace, prototypeName);

		JcrSession destSession = brix.getCurrentSession(workspace.getId());

		JcrUtil.cloneNodes(nodes, destSession.getRootNode());
		destSession.save();
	}

	public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel)
	{
		IBrixTab tabs[] = new IBrixTab[] { new Tab(new Model<String>("Prototypes"), workspaceModel) };
		return Arrays.asList(tabs);
	}

	static class Tab extends AbstractWorkspaceTab
	{
		public Tab(IModel<String> title, IModel<Workspace> workspaceModel)
		{
			super(title, workspaceModel, 50);
		}

		@Override
		public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
		{
			return new ManagePrototypesPanel(panelId, workspaceModel);
		}
	};

	private String getCommonParentPath(List<JcrNode> nodes)
	{
		Path current = null;
		String sitePath = SitePlugin.get().getSiteRootPath();
		for (JcrNode node : nodes)
		{
			if (node.getPath().startsWith(sitePath) && node instanceof GlobalContainerNode == false)
			{
				if (current == null)
				{
					current = new Path(node.getPath()).parent();
				}
				else
				{
					Path another = new Path(node.getPath()).parent();

					Path common = Path.ROOT;

					Iterator<String> i1 = current.iterator();
					Iterator<String> i2 = another.iterator();
					while (i1.hasNext() && i2.hasNext())
					{
						String s1 = i1.next();
						String s2 = i2.next();
						if (Objects.equal(s1, s2))
						{
							common = common.append(new Path(s1));
						}
						else
						{
							break;
						}
					}

					current = common;
				}
			}
		}

		if (current == null)
		{
			current = Path.ROOT;
		}

		return current.toString();
	}

	public void restoreNodes(List<JcrNode> nodes, final JcrNode targetRootNode)
	{
		if (nodes.isEmpty())
		{
			throw new IllegalStateException("List 'nodes' must contain at least one node.");
		}

		ParentLimiter limiter = null;

		// targetRootNode is only applicable for regular Site nodes (not even
		// global container)

		final String siteRoot = SitePlugin.get().getSiteRootPath();

		if (targetRootNode.getDepth() > 0)
		{
			final String commonParent = getCommonParentPath(nodes);
			limiter = new ParentLimiter()
			{
				public boolean isFinalParent(JcrNode node, JcrNode parent)
				{					
					if (node.getPath().startsWith(siteRoot) && node instanceof GlobalContainerNode == false)
					{
						return parent.getPath().equals(commonParent);
					}
					else
					{
						return parent.getDepth() == 0;
					}
				}
			};
		}

		TargetRootNodeProvider provider = new TargetRootNodeProvider()
		{
			public JcrNode getTargetRootNode(JcrNode node)
			{
				if (node.getPath().startsWith(siteRoot) && node instanceof GlobalContainerNode == false)
				{
					return targetRootNode;
				}
				else
				{
					return targetRootNode.getSession().getRootNode();
				}
			}
		};

		JcrUtil.cloneNodes(nodes, provider, limiter);
		targetRootNode.getSession().save();
	}

	public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
	{

	}

	public String getUserVisibleName(Workspace workspace, boolean isFrontend)
	{
		return "Prototype " + getPrototypeName(workspace);
	}

	public boolean isPluginWorkspace(Workspace workspace)
	{
		return isPrototypeWorkspace(workspace);
	}

	public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
	{
		if (isFrontend)
		{
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
			return brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
		}
		else
		{
			return null;
		}
	}
}
