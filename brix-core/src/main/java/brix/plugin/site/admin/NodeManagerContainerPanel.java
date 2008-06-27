package brix.plugin.site.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.DefaultTreeState;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.auth.Action;
import brix.jcr.JcrUtil;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.folder.FolderNode;
import brix.plugin.site.tree.SiteNodeFilter;
import brix.web.picker.common.TreeAwareNode;
import brix.web.tree.AbstractTreeModel;
import brix.web.tree.JcrTreeNode;
import brix.web.tree.TreeNode;
import brix.web.util.AbstractModel;
import brix.workspace.Workspace;

public class NodeManagerContainerPanel extends NodeManagerPanel
{

	private Component<?> editor;

	private final BaseTree tree;
	private IModel<Workspace> workspaceModel;

	// used to detect whether workspace was changed between the requests (node
	// needs to be updated)
	// or node has been changed (workspace needs to be updated)
	private String oldWorkspaceId;

	private static BrixNode getRootNode(IModel<Workspace> workspaceModel)
	{
		BrixNode root = (BrixNode) Brix.get().getCurrentSession(workspaceModel.getObject().getId()).getItem(
				SitePlugin.get().getSiteRootPath());
		return root;
	}

	@Override
	protected void onBeforeRender()
	{
		Workspace workspace = workspaceModel.getObject();
		BrixNode node = getModelObject();

		String nodeWorkspaceName = node.getSession().getWorkspace().getName();
		if (!nodeWorkspaceName.equals(workspace.getId()))
		{
			// we have to either update node or workspace
			if (oldWorkspaceId != null && workspace.getId().equals(oldWorkspaceId))
			{
				// the node changed, need to update the workspace
				Workspace newWorkspace = node.getBrix().getWorkspaceManager().getWorkspace(nodeWorkspaceName);
				workspaceModel.setObject(newWorkspace);
			}
			else
			{
				// the workspace has changed, update the node
				// 1 try to get node with same UUID, 2 try to get node with same
				// path, 3 get root node
				JcrSession newSession = node.getBrix().getCurrentSession(workspace.getId());
				String uuid = node.getUUID();
				BrixNode newNode = JcrUtil.getNodeByUUID(newSession, uuid);
				if (newNode == null)
				{
					String path = node.getPath();
					if (newSession.getRootNode().hasNode(path.substring(1)))
					{
						newNode = (BrixNode)newSession.getItem(path);
					}
				}
				if (newNode == null)
				{
					newNode = getRootNode(workspaceModel);
				}
				selectNode(newNode);
				tree.invalidateAll();
				tree.getTreeState().expandNode(((TreeModel)tree.getModelObject()).getRoot());
			}
		}
		;

		super.onBeforeRender();

		oldWorkspaceId = workspace.getId();
	}

	public NodeManagerContainerPanel(String id, IModel<Workspace> workspaceModel)
	{
		super(id, new BrixNodeModel(getRootNode(workspaceModel)));
		this.workspaceModel = workspaceModel;

		editor = new WebMarkupContainer<Void>(EDITOR_ID);
		add(editor);

		setupDefaultEditor();

		add(tree = new Tree("tree", new TreeModel()));

		WebMarkupContainer<Void> createNodesContainer = new WebMarkupContainer<Void>("createNodesContainer")
		{
			@Override
			public boolean isVisible()
			{
				BrixNode folderNode = getNewNodeParent().getObject();
				Action action = new SiteNodeAction(Action.Context.ADMINISTRATION, SiteNodeAction.Type.NODE_ADD_CHILD,
						folderNode);
				return folderNode.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
			}
		};
		add(createNodesContainer);

		createNodesContainer.add(new ListView<SiteNodePluginEntry>("createNodes", createNodesModel)
		{
			@Override
			protected void populateItem(final ListItem<SiteNodePluginEntry> item)
			{
				Link<Void> link;
				item.add(link = new Link<Void>("link")
				{
					@Override
					public void onClick()
					{
						SiteNodePlugin plugin = item.getModelObject().getPlugin();
						final Component<?> currentEditor = getEditor();

						// remember the last editor that is not a create node
						// panel
						if (lastEditor == null || currentEditor.getMetaData(EDITOR_NODE_TYPE) == null)
						{
							lastEditor = currentEditor;
						}
						SimpleCallback goBack = new SimpleCallback()
						{
							public void execute()
							{
								setupEditor(lastEditor);
							}
						};
						Panel<?> panel = plugin.newCreateNodePanel(EDITOR_ID, getNewNodeParent(), goBack);
						panel.setMetaData(EDITOR_NODE_TYPE, plugin.getNodeType());
						setupEditor(panel);
					}

					@Override
					protected void onComponentTag(ComponentTag tag)
					{
						super.onComponentTag(tag);
						SiteNodePlugin plugin = item.getModelObject().getPlugin();
						String editorNodeType = getEditor().getMetaData(EDITOR_NODE_TYPE);
						if (plugin.getNodeType().equals(editorNodeType))
						{
							CharSequence klass = tag.getString("class");
							if (klass == null)
							{
								klass = "selected";
							}
							else
							{
								klass = klass + " selected";
							}
							tag.put("class", klass);
						}
					}
				});
				item.add(new WebMarkupContainer<Void>("separator")
				{
					@Override
					public boolean isVisible()
					{
						return item.getIndex() != createNodesModel.getObject().size() - 1;
					}
				});
				IModel<BrixNode> parent = getNewNodeParent();
				SiteNodePlugin plugin = item.getModelObject().getPlugin();
				link.add(new Label<String>("label", plugin.newCreateNodeCaptionModel(parent)));
			}

		}.setReuseItems(false));
	}

	private Component<?> lastEditor;

	private static MetaDataKey<String> EDITOR_NODE_TYPE = new MetaDataKey<String>()
	{
	};

	private static class SiteNodePluginEntry implements Serializable
	{

		private final String nodeType;

		public SiteNodePluginEntry(SiteNodePlugin plugin)
		{
			this.nodeType = plugin.getNodeType();
		}

		public SiteNodePlugin getPlugin()
		{
			return SitePlugin.get().getNodePluginForType(nodeType);
		}
	};

	private IModel<List<SiteNodePluginEntry>> createNodesModel = new LoadableDetachableModel<List<SiteNodePluginEntry>>()
	{
		@Override
		protected List<SiteNodePluginEntry> load()
		{
			List<SiteNodePluginEntry> result = new ArrayList<SiteNodePluginEntry>();
			for (SiteNodePlugin plugin : SitePlugin.get().getNodePlugins())
			{
				IModel<BrixNode> parent = getNewNodeParent();
				if (plugin.newCreateNodeCaptionModel(parent) != null)
				{
					result.add(new SiteNodePluginEntry(plugin));
				}
			}
			return result;
		}
	};

	private IModel<BrixNode> getNewNodeParent()
	{
		BrixNode current = getModelObject();
		if (current instanceof FolderNode)
		{
			return getModel();
		}
		else
		{
			return new BrixNodeModel((BrixNode) current.getParent());
		}
	};

	private class Tree extends LinkTree
	{

		public Tree(String id, TreeModel model)
		{
			super(id, model);
			setLinkType(LinkType.REGULAR);
			getTreeState().expandNode(model.getRoot());
		}

		@Override
		protected Component<?> newJunctionLink(MarkupContainer parent, String id, Object node)
		{
			LinkType old = getLinkType();
			setLinkType(LinkType.AJAX);
			Component<?> c = super.newJunctionLink(parent, id, node);
			setLinkType(old);
			return c;
		}
		
		@Override
		protected IModel getNodeTextModel(final IModel nodeModel)
		{
			return new AbstractModel<String>()
			{
				@Override
				public String getObject()
				{
					JcrTreeNode node = (JcrTreeNode) nodeModel.getObject();
					BrixNode n = node.getNodeModel().getObject();
					return n.getUserVisibleName();
				}
			};
		}
		
		@Override
		protected ITreeState newTreeState()
		{
			return new TreeState();
		}
	};

	private class TreeState extends DefaultTreeState
	{
		@Override
		public void selectNode(Object node, boolean selected)
		{
			if (selected)
			{
				JcrTreeNode n = (JcrTreeNode) node;
				NodeManagerContainerPanel.this.setModel(n.getNodeModel());
				setupDefaultEditor();
				expandParents(n.getNodeModel().getObject());
			}
		}

		private void expandParents(BrixNode node)
		{
			BrixNode parent = (BrixNode) node.getParent();
			while (parent.getDepth() > 0)
			{
				expandNode(getTreeNode(parent));
				parent = (BrixNode) parent.getParent();
			}
		}

		@Override
		public boolean isNodeSelected(Object node)
		{
			JcrTreeNode n = (JcrTreeNode) node;
			IModel<BrixNode> model = n.getNodeModel();
			return model != null && model.equals(NodeManagerContainerPanel.this.getModel());
		}

		@Override
		public Collection<Object> getSelectedNodes()
		{
			JcrTreeNode node = getTreeNode(getModelObject());
			return Arrays.asList(new Object[] { node });
		}
	};

	private class TreeModel extends AbstractTreeModel
	{
		public TreeNode getRoot()
		{			
			return getTreeNode(SitePlugin.get().getSiteRootNode(workspaceModel.getObject().getId())); 
		}
	};
	
	private static final SiteNodeFilter NODE_FILTER = new SiteNodeFilter(false, null); 

	private JcrTreeNode getTreeNode(BrixNode node)
	{
		return TreeAwareNode.Util.getTreeNode(node, NODE_FILTER);
	}
	
	public void selectNode(BrixNode node)
	{
		tree.getTreeState().selectNode(getTreeNode(node), true);
	}

	public void updateTree()
	{
		tree.invalidateAll();
		tree.updateTree();
	}

	private Component<?> getEditor()
	{
		return get(EDITOR_ID);
	};

	private void setupEditor(Component<?> newEditor)
	{
		editor.replaceWith(newEditor);
		editor = newEditor;
	}

	private void setupDefaultEditor()
	{
		setupEditor(new NodeManagerEditorPanel(EDITOR_ID, getModel()));
	}

	private static final String EDITOR_ID = "editor";

}
