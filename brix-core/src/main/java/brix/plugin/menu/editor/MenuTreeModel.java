package brix.plugin.menu.editor;

import org.apache.wicket.model.IDetachable;

import brix.plugin.menu.Menu.Entry;
import brix.web.tree.AbstractTreeModel;
import brix.web.tree.TreeNode;

public class MenuTreeModel extends AbstractTreeModel implements IDetachable
{
    public MenuTreeModel(Entry root)
    {
        if (root == null)
        {
            throw new IllegalArgumentException("Argument 'root' may not be null.");
        }
        this.root = new MenuTreeNode(root);
    }

    private final MenuTreeNode root;

    public TreeNode getRoot()
    {
        return root;
    }


    public void detach()
    {
        root.detach();
    }

}
