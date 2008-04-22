package brix.plugin.menu.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import org.apache.wicket.model.IDetachable;

import brix.plugin.menu.Menu.Entry;

public class MenuTreeNode implements TreeNode, IDetachable
{
    private final Entry entry;
    private final MenuTreeNode parent;

    public Entry getEntry()
    {
        return entry;
    }

    public MenuTreeNode(Entry entry)
    {
        this(entry, null);
    }

    public MenuTreeNode(Entry entry, MenuTreeNode parent)
    {
        if (entry == null)
        {
            throw new IllegalArgumentException("Argument 'entry' may not be null.");
        }
        this.entry = entry;
        this.parent = parent;
    }

    private Map<Entry, MenuTreeNode> entryCache = new HashMap<Entry, MenuTreeNode>();

    public MenuTreeNode nodeForEntry(Entry e) {
        MenuTreeNode res = entryCache.get(e);
        if (res == null)
        {
            res = new MenuTreeNode(e, this);
            entryCache.put(e, res);
        }
        return res;
    }
    
    public void detach()
    {
        List<Entry> list = new ArrayList<Entry>(entryCache.keySet());
        for (Entry e : list)
        {
            if (entry.getChildren().contains(e) == false) 
            {
                entryCache.remove(e);
            }
        }
    }

    public Enumeration< ? > children()
    {
        List<MenuTreeNode> entries = new ArrayList<MenuTreeNode>();

        for (Entry e : entry.getChildren())
        {
            entries.add(nodeForEntry(e));
        }

        return Collections.enumeration(entries);
    }

    public boolean getAllowsChildren()
    {
        return true;
    }

    public TreeNode getChildAt(int childIndex)
    {
        return nodeForEntry(entry.getChildren().get(childIndex));
    }

    public int getChildCount()
    {
        return entry.getChildren().size();
    }

    public int getIndex(TreeNode node)
    {
        Entry that = ((MenuTreeNode)node).getEntry();
        return entry.getChildren().indexOf(that);
    }

    public TreeNode getParent()
    {
        return parent;
    }

    public boolean isLeaf()
    {
        return entry.getChildren().isEmpty();
    }
    
    @Override
    public String toString()
    {
        return entry.toString();
    }

}
