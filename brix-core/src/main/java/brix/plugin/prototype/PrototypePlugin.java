package brix.plugin.prototype;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Objects;

import brix.Brix;
import brix.Path;
import brix.Plugin;
import brix.jcr.JcrUtil;
import brix.jcr.JcrUtil.ParentLimiter;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.web.tab.AbstractWorkspaceTab;
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

		JcrUtil.cloneNodes(nodes, destSession.getRootNode(), ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
		destSession.save();
	}

	public List<ITab> newTabs(IModel<Workspace> workspaceModel)
	{
		ITab tabs[] = new ITab[] { new Tab(new Model<String>("Prototypes"), workspaceModel) };
		return Arrays.asList(tabs);
	}
		
	static class Tab extends AbstractWorkspaceTab
	{
		public Tab(IModel<String> title, IModel<Workspace> workspaceModel)
		{
			super(title, workspaceModel);
		}

		@Override
		public Panel<?> newPanel(String panelId, IModel<Workspace> workspaceModel)
		{
			return new ManagePrototypesPanel(panelId, workspaceModel);
		}
	};

	private String getCommonParentPath(List<JcrNode> nodes)
	{
		Path current = null;
		for (JcrNode node : nodes)
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

		return current.toString();
	}

	public void restoreNodes(List<JcrNode> nodes, JcrNode targetRootNode)
	{
		if (nodes.isEmpty())
		{
			throw new IllegalStateException("List 'nodes' must contain at least one node.");
		}

		ParentLimiter limiter = null;

		if (targetRootNode.getDepth() > 0)
		{
			final String commonParent = getCommonParentPath(nodes);
			limiter = new ParentLimiter()
			{
				public boolean isFinalParent(JcrNode node, JcrNode parent)
				{
					return parent.getPath().equals(commonParent);
				}
			};
		}

		JcrUtil.cloneNodes(nodes, targetRootNode, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, limiter);
		targetRootNode.save();
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

	private static final ResourceReference ICON = new ResourceReference(PrototypePlugin.class, "layers.png");
}
