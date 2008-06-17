package brix.plugin.site.page.fragment;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.BrixNodeModel;
import brix.Plugin;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.tab.AbstractWorkspaceTab;
import brix.workspace.Workspace;

public class FragmentPlugin implements Plugin
{
	private static final String FRAGMENTS_NODE_NAME = Brix.NS_PREFIX + "fragments";

	private final Brix brix;

	private static final String PLUGIN_ID = "brix.plugin.fragment.FragmentPlugin";

	public FragmentPlugin(Brix brix)
	{
		super();
		this.brix = brix;
	}

	public String getId()
	{
		return PLUGIN_ID;
	}

	public String getUserVisibleName(Workspace workspace, boolean isFrontend)
	{
		return null;
	}

	public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
	{
		return null;
	}

	public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
	{
		JcrNode root = (JcrNode) workspaceSession.getItem(brix.getRootPath());
		JcrNode fragments;
		if (root.hasNode(FRAGMENTS_NODE_NAME))
		{
			fragments = root.getNode(FRAGMENTS_NODE_NAME);
		}
		else
		{
			fragments = root.addNode(FRAGMENTS_NODE_NAME, "nt:unstructured");
		}
		if (!fragments.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE))
		{
			fragments.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
		}
		if (!fragments.isNodeType(FragmentsContainerNode.TYPE))
		{
			fragments.addMixin(FragmentsContainerNode.TYPE);
		}

	}

	public boolean isPluginWorkspace(Workspace workspace)
	{
		return false;
	}

	public List<ITab> newTabs(final IModel<Workspace> workspaceModel)
	{
		ITab tabs[] = new ITab[] { new Tab(new Model<String>("Fragments"), workspaceModel) };
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
			Brix brix = Brix.get();
			JcrSession session = brix.getCurrentSession(workspaceModel.getObject().getId());
			BrixNode root = (BrixNode) session.getItem(brix.getRootPath() + "/" + FRAGMENTS_NODE_NAME);
			return new FragmentManagerPanel(panelId, new BrixNodeModel(root));
		}
	};

	public static final FragmentsContainerNode getContainerNode(Brix brix, String workspaceName)
	{
		JcrSession session = brix.getCurrentSession(workspaceName);
		return (FragmentsContainerNode) ((JcrNode) session.getItem(brix.getRootPath())).getNode(FRAGMENTS_NODE_NAME);
	}

}
