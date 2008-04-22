package brix.web.admin.navigation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Plugin;
import brix.web.tree.AbstractTreeModel;

public class NavigationTreeModel extends AbstractTreeModel
{

    private RootNode rootNode = new RootNode();

    public Object getRoot()
    {
        return rootNode;
    }
    
    public Object getParent(Object node)
    {
        if (rootNode.getChildren().contains(node))
        {
            return rootNode;
        }
        else
        {
            return ((TreeNode)node).getParent();
        }
    }
    
    private class RootNode implements TreeNode, Serializable
    {

        private List<NavigationTreeNode> children;

        public List<NavigationTreeNode> getChildren()
        {
            if (children == null)
            {
                children = new ArrayList<NavigationTreeNode>();
                Brix brix = BrixRequestCycle.Locator.getBrix();
                for (Plugin p : brix.getPlugins())
                {
                    NavigationTreeNode node = p.newNavigationTreeNode(workspaceName);
                    if (node != null)
                    {
                        children.add(node);
                    }
                }
            }
            return children;
        }

        public Enumeration<NavigationTreeNode> children()
        {
            return Collections.enumeration(getChildren());
        }

        public boolean getAllowsChildren()
        {
            return true;
        }

        public TreeNode getChildAt(int childIndex)
        {
            return getChildren().get(childIndex);
        }

        public int getChildCount()
        {
            return getChildren().size();
        }

        public int getIndex(TreeNode node)
        {
            return getChildren().indexOf(node);
        }

        public TreeNode getParent()
        {
            return null;
        }

        public boolean isLeaf()
        {
            return false;
        }
    };

    private final String workspaceName;

    public NavigationTreeModel(String workspaceName)
    {
        this.workspaceName = workspaceName;
    }

}
