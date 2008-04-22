package brix.plugin.menu.editor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.wicket.model.IDetachable;

import brix.plugin.menu.Menu.Entry;

public class MenuTreeModel implements TreeModel, IDetachable, Serializable
{
    private final List<TreeModelListener> listeners = new ArrayList<TreeModelListener>(1);

    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(l);
    }

    public MenuTreeModel(Entry root)
    {
        if (root == null)
        {
            throw new IllegalArgumentException("Argument 'root' may not be null.");
        }
        this.root = new MenuTreeNode(root);
    }

    private final MenuTreeNode root;

    public Object getChild(Object parent, int index)
    {
        MenuTreeNode node = (MenuTreeNode)parent;
        return node.getChildAt(index);
    }

    public int getChildCount(Object parent)
    {
        MenuTreeNode node = (MenuTreeNode)parent;
        return node.getChildCount();
    }

    public int getIndexOfChild(Object parent, Object child)
    {
        MenuTreeNode node = (MenuTreeNode)parent;
        return node.getIndex((MenuTreeNode)child);
    }

    public Object getRoot()
    {
        return root;
    }

    public boolean isLeaf(Object node)
    {
        MenuTreeNode n = (MenuTreeNode)node;
        return n.isLeaf();
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(l);
    }

    private TreePath pathFromNode(MenuTreeNode node)
    {
        List<MenuTreeNode> l = new ArrayList<MenuTreeNode>();
        for (MenuTreeNode n = node; n != null; n = (MenuTreeNode)n.getParent())
        {
            l.add(0, n);
        }
        return new TreePath(l.toArray(new MenuTreeNode[l.size()]));
    }

    public void nodeChanged(MenuTreeNode node)
    {
        MenuTreeNode parent = (MenuTreeNode)node.getParent();
        TreeModelEvent e = new TreeModelEvent(this, pathFromNode(parent), new int[] { parent
                .getIndex(node) }, new Object[] { node });
        for (TreeModelListener l : listeners)
        {
            l.treeNodesChanged(e);
        }
    }

    public void nodeInserted(MenuTreeNode node)
    {
        MenuTreeNode parent = (MenuTreeNode)node.getParent();
        TreeModelEvent e = new TreeModelEvent(this, pathFromNode(parent), new int[] { parent
                .getIndex(node) }, new Object[] { node });
        for (TreeModelListener l : listeners)
        {
            l.treeNodesInserted(e);
        }
    }

    // must be invoked *before* the node is actually deleted
    public void nodeDeleted(MenuTreeNode node)
    {
        MenuTreeNode parent = (MenuTreeNode)node.getParent();
        TreeModelEvent e = new TreeModelEvent(this, pathFromNode(parent), new int[] { parent
                .getIndex(node) }, new Object[] { node });
        for (TreeModelListener l : listeners)
        {
            l.treeNodesRemoved(e);
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue)
    {

    }

    public void detach()
    {
        root.detach();
    }

}
