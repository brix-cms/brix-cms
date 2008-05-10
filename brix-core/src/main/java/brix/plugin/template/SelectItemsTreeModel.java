package brix.plugin.template;

import javax.swing.tree.TreeNode;

import brix.BrixRequestCycle;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.tree.AbstractTreeModel;
import brix.web.tree.AbstractTreeNode;

public class SelectItemsTreeModel extends AbstractTreeModel
{
    public class SelectItemsTreeNode extends AbstractTreeNode
    {

        public SelectItemsTreeNode(JcrNode node)
        {
            super(node);
        }

        @Override
        protected AbstractTreeNode newTreeNode(JcrNode node)
        {
            return new SelectItemsTreeNode(node);
        }

        @Override
        public TreeNode getParent()
        {
            if (this.equals(root))
            {
                return null;
            }
            else
            {
                return super.getParent();
            }
        }

        @Override
        protected boolean displayFoldersOnly()
        {
            return false;
        }

        public BrixNode getNode()
        {
            return (BrixNode)getNodeModel().getObject();
        }
    };

    public SelectItemsTreeNode treeNodeFor(JcrNode node)
    {
        return new SelectItemsTreeNode(node);
    }

    private SelectItemsTreeNode root;

    public SelectItemsTreeModel(String workspaceName)
    {
        JcrSession session = BrixRequestCycle.Locator.getSession(workspaceName);

        root = new SelectItemsTreeNode((JcrNode)session.getItem(SitePlugin.get().getSiteRootPath()));
    }

    public Object getRoot()
    {
        return root;
    }

}
