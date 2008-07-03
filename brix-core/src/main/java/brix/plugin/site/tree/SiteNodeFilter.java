package brix.plugin.site.tree;

import brix.jcr.wrapper.BrixNode;
import brix.web.tree.NodeFilter;

public class SiteNodeFilter implements NodeFilter
{

	private final boolean foldersOnly;
	private final NodeFilter customFilter;

	public SiteNodeFilter(boolean foldersOnly, NodeFilter customFilter)
	{
		this.foldersOnly = foldersOnly;
		this.customFilter = customFilter;
	}

	public boolean isNodeAllowed(BrixNode node)
	{
		if (node == null)
		{
			return false;
		}
		else if (foldersOnly && !node.isFolder())
		{
			return false;
		}
		else if (customFilter != null && !customFilter.isNodeAllowed(node))
		{
			return false;
		}
		return true;
	}
	
}
