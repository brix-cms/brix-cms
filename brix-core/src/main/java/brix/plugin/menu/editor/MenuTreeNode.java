package brix.plugin.menu.editor;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.util.lang.Objects;

import brix.plugin.menu.Menu.Entry;
import brix.web.tree.TreeNode;

public class MenuTreeNode implements TreeNode
{
	private final Entry entry;

	public Entry getEntry()
	{
		return entry;
	}

	public MenuTreeNode(Entry entry)
	{
		if (entry == null)
		{
			throw new IllegalArgumentException("Argument 'entry' may not be null.");
		}
		this.entry = entry;
	}

	public void detach()
	{
	}

	public List<? extends TreeNode> getChildren()
	{
		List<MenuTreeNode> children = new ArrayList<MenuTreeNode>();
		for (Entry e : entry.getChildren())
		{
			children.add(new MenuTreeNode(e));
		}
		return children;
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

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj instanceof MenuTreeNode == false)
		{
			return false;
		}
		return Objects.equal(entry, ((MenuTreeNode) obj).entry);
	}

	@Override
	public int hashCode()
	{
		return entry.hashCode();
	}
}
