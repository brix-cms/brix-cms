package brix.plugin.prototype;

import brix.Brix;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.web.tree.AbstractJcrTreeNode;
import brix.web.tree.AbstractTreeModel;

public class SelectItemsTreeModel extends AbstractTreeModel
{
    public class SelectItemsTreeNode extends AbstractJcrTreeNode
    {

        public SelectItemsTreeNode(BrixNode node)
        {
            super(node);
        }

        @Override
        protected AbstractJcrTreeNode newTreeNode(BrixNode node)
        {
            return new SelectItemsTreeNode(node);
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

    public SelectItemsTreeNode treeNodeFor(BrixNode node)
    {
        return new SelectItemsTreeNode(node);
    }

    private SelectItemsTreeNode root;

    public SelectItemsTreeModel(String workspaceName)
    {
        JcrSession session = Brix.get().getCurrentSession(workspaceName);

        root = new SelectItemsTreeNode((BrixNode)session.getItem(SitePlugin.get().getSiteRootPath()));
    }

    public Object getRoot()
    {
        return root;
    }

}
