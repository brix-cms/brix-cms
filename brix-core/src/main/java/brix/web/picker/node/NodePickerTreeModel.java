package brix.web.picker.node;

import brix.BrixRequestCycle;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.tree.AbstractTreeModel;
import brix.web.tree.AbstractJcrTreeNode;

public abstract class NodePickerTreeModel extends AbstractTreeModel
{
    public class NodePickerTreeNode extends AbstractJcrTreeNode
    {

        public NodePickerTreeNode(JcrNode node)
        {
            super(node);
        }

        @Override
        protected AbstractJcrTreeNode newTreeNode(JcrNode node)
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

    public NodePickerTreeNode treeNodeFor(JcrNode node)
    {
        return new NodePickerTreeNode(node);
    }

    protected abstract boolean displayFoldersOnly();

    private NodePickerTreeNode root;

    public NodePickerTreeModel(String workspaceName)
    {
        JcrSession session = BrixRequestCycle.Locator.getSession(workspaceName);

        root = new NodePickerTreeNode((JcrNode)session.getItem(SitePlugin.get().getSiteRootPath()));
    }

    public Object getRoot()
    {
        return root;
    }

}
