package brix.plugin.site.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.ReferentialIntegrityException;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.DefaultTreeState;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.LinkTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.Brix;
import brix.BrixNodeModel;
import brix.Path;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.auth.SiteNodeAction.Type;
import brix.web.tree.AbstractJcrTreeNode;
import brix.web.tree.AbstractTreeModel;
import brix.web.util.PathLabel;

public class NodeManagerContainerPanel extends NodeManagerPanel
{

    private Component editor;
    private final String workspaceName;
    private final BaseTree tree;

    private static BrixNode getRootNode(String workspaceName)
    {
    	BrixNode root = (BrixNode) Brix.get().getCurrentSession(workspaceName).getItem(SitePlugin.get().getSiteRootPath());
    	return root;
    }
    
    public NodeManagerContainerPanel(String id, String workspaceName)
    {
        super(id, new BrixNodeModel(getRootNode(workspaceName)));
        this.workspaceName = workspaceName;
        
        Path root = new Path(SitePlugin.get().getSiteRootPath());
        add(new PathLabel("path2", new PropertyModel(this, "node.path"), root)
        {
            @Override
            protected void onPathClicked(Path path)
            {
                BrixNode node = (BrixNode)getNode().getSession().getItem(path.toString());
                selectNode(node);
            }
        });

        add(new Link("rename")
        {
            @Override
            public void onClick()
            {
                final Component< ? > old = editor;
                Panel<BrixNode> renamePanel = new RenamePanel(EDITOR_ID,
                    NodeManagerContainerPanel.this.getModel())
                {
                    @Override
                    protected void onLeave()
                    {
                        setupEditor(old);
                    }
                };
                setupEditor(renamePanel);
            }

            @Override
            public boolean isVisible()
            {
                Action action = new SiteNodeAction(Context.ADMINISTRATION, Type.NODE_RENAME,
                    getNode());

                BrixNode node = NodeManagerContainerPanel.this.getModelObject();
                String path = node.getPath();
                String web = SitePlugin.get().getSiteRootPath();
                Brix brix = node.getBrix();
                return brix.getAuthorizationStrategy().isActionAuthorized(action) &&
                    path.length() > web.length() && path.startsWith(web);
            }
        });

        add(new Link("makeVersionable")
        {
            @Override
            public void onClick()
            {
                if (!getNode().isNodeType("mix:versionable"))
                {
                    getNode().addMixin("mix:versionable");
                    getNode().save();
                    getNode().checkin();
                }
            }

            @Override
            public boolean isVisible()
            {
                Action action = new SiteNodeAction(Context.ADMINISTRATION, Type.NODE_EDIT,
                    getNode());
                return getNode() != null && getNode().isNodeType("nt:file") &&
                    !getNode().isNodeType("mix:versionable") &&
                    getNode().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
            }
        });

        add(new Link("delete")
        {

            @Override
            public void onClick()
            {
                BrixNode node = getNode();
                BrixNode parent = (BrixNode)node.getParent();

                selectNode(parent);

                node.remove();

                try
                {
                    parent.save();
                }
                catch (JcrException e)
                {
                    if (e.getCause() instanceof ReferentialIntegrityException)
                    {
                        parent.getSession().refresh(false);
                        NodeManagerContainerPanel.this.getModel().detach();
                        // parent.refresh(false);
                        getSession().error(
                            "Couldn't delete node. Other nodes contain references to this node.");
                        selectNode(getNode());
                    }
                    else
                    {
                        throw e;
                    }
                }
            }

            @Override
            public boolean isVisible()
            {
                Action action = new SiteNodeAction(Context.ADMINISTRATION, Type.NODE_DELETE,
                    getNode());
                Brix brix = getNode().getBrix();
                String path = getNode().getPath();
                return path.startsWith(SitePlugin.get().getSiteRootPath()) &&
                    path.length() > SitePlugin.get().getSiteRootPath().length() &&
                    brix.getAuthorizationStrategy().isActionAuthorized(action);
            }

        });

        editor = new WebMarkupContainer(EDITOR_ID);
        add(editor);

        setupTabbedPanel();

        add(new SessionFeedbackPanel("sessionFeedback"));
                
        add(tree = new Tree("tree", new TreeModel()));
    }
    
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
    			setupTabbedPanel();
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
    		return Arrays.asList(new Object [] { node });
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
			return getNodeModel().getObject().getName();
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
    
    private List<ITab> getTabs(IModel<BrixNode> nodeModel)
    {
        Collection<ManageNodeTabFactory> factories = nodeModel.getObject().getBrix().getConfig()
            .getRegistry().lookupCollection(ManageNodeTabFactory.POINT);
        if (factories != null && !factories.isEmpty())
        {
            int tabCount = 0;
            class Entry
            {
                ManageNodeTabFactory factory;
                List<ITab> tabs;
            }
            ;
            List<Entry> list = new ArrayList<Entry>();
            for (ManageNodeTabFactory f : factories)
            {
                List<ITab> tabs = f.getManageNodeTabs(nodeModel);
                if (tabs != null && !tabs.isEmpty())
                {
                    Entry e = new Entry();
                    e.factory = f;
                    e.tabs = tabs;
                    tabCount += tabs.size();
                    list.add(e);
                }
            }
            Collections.sort(list, new Comparator<Entry>()
            {
                public int compare(Entry o1, Entry o2)
                {
                    return o2.factory.getPriority() - o1.factory.getPriority();
                }
            });
            List<ITab> result = new ArrayList<ITab>(tabCount);
            for (Entry e : list)
            {
                result.addAll(e.tabs);
            }
            return result;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public void selectNode(BrixNode node)
    {
    	tree.getTreeState().selectNode(new SiteTreeNode(node), true);
    }

    private void setupEditor(Component newEditor)
    {
        editor.replaceWith(newEditor);
        editor = newEditor;
    }

    private void setupTabbedPanel()
    {
        setupEditor(new NodeManagerTabbedPanel(EDITOR_ID, getTabs(getModel())));
    }

    private static final String EDITOR_ID = "editor";

    private static class SessionFeedbackPanel extends FeedbackPanel
    {

        public SessionFeedbackPanel(String id)
        {
            super(id, new Filter());
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean isVisible()
        {
            List messages = (List)getFeedbackMessagesModel().getObject();
            return messages != null && !messages.isEmpty();
        }

        private static class Filter implements IFeedbackMessageFilter
        {
            public boolean accept(FeedbackMessage message)
            {
                return message.getReporter() == null;
            }
        };
    };

}
