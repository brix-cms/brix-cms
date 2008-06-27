package brix.web.picker.node;

import brix.Brix;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.tree.AbstractJcrTreeNode;
import brix.web.tree.AbstractTreeModel;
import brix.web.tree.TreeNode;

public abstract class NodePickerTreeModel extends AbstractTreeModel
{
    public class NodePickerTreeNode extends AbstractJcrTreeNode
    {

        public NodePickerTreeNode(BrixNode node)
        {
            super(node);
        }

        @Override
        protected AbstractJcrTreeNode newTreeNode(BrixNode node)
        {
            return new NodePickerTreeNode(node);
        }

        @Override
        protected boolean displayFoldersOnly()
        {
            return NodePickerTreeModel.this.displayFoldersOnly();
        }

        public BrixNode getNode()
        {
            return (BrixNode)getNodeModel().getObject();
        }
    };

    public NodePickerTreeNode treeNodeFor(BrixNode node)
    {
        return new NodePickerTreeNode(node);
    }

    protected abstract boolean displayFoldersOnly();

    private NodePickerTreeNode root;

    public NodePickerTreeModel(String workspaceName)
    {
        JcrSession session = Brix.get().getCurrentSession(workspaceName);

        root = new NodePickerTreeNode((BrixNode)session.getItem(SitePlugin.get().getSiteRootPath()));
    }

    public TreeNode getRoot()
    {
        return root;
    }

}
