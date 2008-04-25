package brix.web.admin.navigation;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.DefaultTreeState;
import org.apache.wicket.markup.html.tree.ExtendedTreeModel;
import org.apache.wicket.markup.html.tree.ITreeState;
import org.apache.wicket.markup.html.tree.ITreeStateListener;
import org.apache.wicket.markup.html.tree.BaseTree.LinkType;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class NavigationPanel extends Panel<Object>
        implements
            Navigation,
            ITreeStateListener
{

    @SuppressWarnings("unchecked")
    public NavigationPanel(String id, String workspaceName)
    {
        super(id);
        treeModel = new NavigationTreeModel(workspaceName);
        add(tree = new NavigationTree("tree", new Model((Serializable)treeModel))
        {
            @Override
            protected ITreeState newTreeState()
            {
                // create curtom tree state that doesn't allow user to deselect a node
                return new DefaultTreeState()
                {
                    @Override
                    public void selectNode(Object node, boolean selected)
                    {
                        if (selected == true)
                        {
                            if (super.isNodeSelected(node))
                            {
                                super.selectNode(node, false);
                            }
                            super.selectNode(node, selected);
                        }
                    }
                };
            }

            @Override
            protected Component newJunctionLink(MarkupContainer parent, String id, Object node)
            {
                LinkType old = getLinkType();
                setLinkType(LinkType.AJAX);
                Component c = super.newJunctionLink(parent, id, node);
                setLinkType(old);
                return c;
            }
        });
        tree.setRootLess(true);
        tree.setLinkType(LinkType.REGULAR);
        tree.getTreeState().addTreeStateListener(this);
    }

    private static class NavigationTree extends BaseTree
    {

        public NavigationTree(String id, IModel< ? > model)
        {
            super(id, model);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Component< ? > newNodeComponent(String id, IModel model)
        {
            NavigationTreeNode node = (NavigationTreeNode)model.getObject();
            return node.newLinkPanel(id, this);
        }

        @Override
        protected String getItemClass(Object node)
        {
            NavigationTreeNode treeNode = (NavigationTreeNode)node;
            if (!getTreeState().getSelectedNodes().isEmpty())
            {
                NavigationTreeNode selected = (NavigationTreeNode)getTreeState().getSelectedNodes()
                    .iterator().next();

                boolean s = selected.equals(node) ||
                    (selected.getParent() != null && selected.getParent().equals(treeNode) && treeNode.getIndex(selected) == -1);
                return s ? getSelectedClass() : null;
            }
            else
            {
                return super.getItemClass(node);
            }
        }
    };

    private final NavigationTreeModel treeModel;
    private final NavigationTree tree;


    public BaseTree getTree()
    {
        return tree;
    }

    public void nodeChanged(NavigationTreeNode node)
    {
        treeModel.nodeChanged(node);
    }

    public void nodeChildrenChanged(NavigationTreeNode node)
    {
        treeModel.nodeChildrenChanged(node);
    }

    public void nodeDeleted(NavigationTreeNode node)
    {
        treeModel.nodeDeleted(node);
    }

    public void nodeInserted(NavigationTreeNode node)
    {
        treeModel.nodeInserted(node);
    }

    public void selectNode(NavigationTreeNode node)
    {        
        tree.getTreeState().selectNode(node, true);
    }

    public void allNodesCollapsed()
    {

    }

    public void allNodesExpanded()
    {

    }

    public void nodeCollapsed(Object node)
    {

    }

    public void nodeExpanded(Object node)
    {

    }

    public void nodeSelected(Object node)
    {
        for (Object n = node; n != null; n = ((ExtendedTreeModel)tree.getModelObject())
            .getParent(n))
        {
            tree.getTreeState().expandNode(n);
        }
        onNodeSelected((NavigationTreeNode)node);
    }

    public void nodeUnselected(Object node)
    {

    }

    protected abstract void onNodeSelected(NavigationTreeNode node);
}
