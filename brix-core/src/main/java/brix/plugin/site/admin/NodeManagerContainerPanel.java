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
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SiteNodePlugin;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.folder.FolderNode;
import brix.web.tree.AbstractJcrTreeNode;
import brix.web.tree.AbstractTreeModel;

public class NodeManagerContainerPanel extends NodeManagerPanel
{

	private Component<?> editor;
	private final String workspaceName;
	private final BaseTree tree;

	private static BrixNode getRootNode(String workspaceName)
	{
		BrixNode root = (BrixNode) Brix.get().getCurrentSession(workspaceName).getItem(
				SitePlugin.get().getSiteRootPath());
		return root;
	}

	public NodeManagerContainerPanel(String id, String workspaceName)
	{
		super(id, new BrixNodeModel(getRootNode(workspaceName)));
		this.workspaceName = workspaceName;

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
						
						// remember the last editor that is not a create node panel
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
				SiteTreeNode n = (SiteTreeNode) node;
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
				expandNode(new SiteTreeNode(parent));
				parent = (BrixNode) parent.getParent();
			}
		}

		@Override
		public boolean isNodeSelected(Object node)
		{
			SiteTreeNode n = (SiteTreeNode) node;
			IModel<BrixNode> model = n.getNodeModel();
			return model != null && model.equals(NodeManagerContainerPanel.this.getModel());
		}

		@Override
		public Collection<Object> getSelectedNodes()
		{
			SiteTreeNode node = new SiteTreeNode(getModelObject());
			return Arrays.asList(new Object[] { node });
		}
	};

	private class SiteTreeNode extends AbstractJcrTreeNode
	{

		public SiteTreeNode(BrixNode node)
		{
			super(node);
		}

		@Override
		protected boolean displayFoldersOnly()
		{
			return false;
		}

		@Override
		protected AbstractJcrTreeNode newTreeNode(BrixNode node)
		{
			return new SiteTreeNode(node);
		}

		@Override
		public String toString()
		{
			SitePlugin sp = SitePlugin.get();
			BrixNode node = getNodeModel().getObject();
			if (sp.getSiteRootPath().equals(node.getPath()))
			{
				return getString("siteRoot");
			}
			else
			{
				return node.getName();
			}
		}

	};

	private class TreeModel extends AbstractTreeModel
	{

		public Object getRoot()
		{
			BrixNode root = getRootNode(workspaceName);
			return new SiteTreeNode(root);
		}

	};

	public void selectNode(BrixNode node)
	{
		tree.getTreeState().selectNode(new SiteTreeNode(node), true);
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
