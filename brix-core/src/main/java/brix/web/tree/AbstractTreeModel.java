package brix.web.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.wicket.markup.html.tree.ExtendedTreeModel;

import brix.web.admin.navigation.NavigationTreeNode;

public abstract class AbstractTreeModel implements ExtendedTreeModel, Serializable
{

    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>(0);

    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(l);
    }

    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(l);
    }

    public Object getChild(Object parent, int index)
    {
        return ((TreeNode)parent).getChildAt(index);
    }

    public int getChildCount(Object parent)
    {
        return ((TreeNode)parent).getChildCount();
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        return ((TreeNode)parent).getIndex((TreeNode)child);
    }

    public boolean isLeaf(Object node)
    {
        return ((TreeNode)node).isLeaf();
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {

    }

    public Object getParent(Object node)
    {
        return ((TreeNode)node).getParent();
    }

    private TreePath pathFromNode(TreeNode node)
    {
        List<TreeNode> l = new ArrayList<TreeNode>();
        for (TreeNode n = node; n != null; n = (TreeNode)getParent(n))
        {
            l.add(0, n);
        }
        return new TreePath(l.toArray(new TreeNode[l.size()]));
    }

    public void nodeChanged(NavigationTreeNode node)
    {
        TreeNode parent = (TreeNode)node.getParent();
        int index = parent.getIndex(node);
        if (index != -1)
        {
            TreeModelEvent e = new TreeModelEvent(this, pathFromNode(parent), new int[] { index },
                    new Object[] { node });
            for (TreeModelListener l : listeners)
            {
                l.treeNodesChanged(e);
            }
        }
    }

    public void nodeInserted(TreeNode node)
    {
        TreeNode parent = (TreeNode)node.getParent();
        int index = parent.getIndex(node);
        if (index != -1)
        {
            TreeModelEvent e = new TreeModelEvent(this, pathFromNode(parent), new int[] { index },
                    new Object[] { node });
            for (TreeModelListener l : listeners)
            {
                l.treeNodesInserted(e);
            }
        }
    }

    // must be invoked *before* the node is actually deleted
    public void nodeDeleted(TreeNode node)
    {
        TreeNode parent = (TreeNode)node.getParent();
        int index = parent.getIndex(node);
        if (index != -1)
        {
            TreeModelEvent e = new TreeModelEvent(this, pathFromNode(parent), new int[] { index },
                    new Object[] { node });
            for (TreeModelListener l : listeners)
            {
                l.treeNodesRemoved(e);
            }
        }
    }

    public void nodeChildrenChanged(TreeNode node)
    {
        TreeModelEvent event = new TreeModelEvent(this, pathFromNode(node));
        for (TreeModelListener l : listeners)
        {
            l.treeStructureChanged(event);
        }
    }
}
