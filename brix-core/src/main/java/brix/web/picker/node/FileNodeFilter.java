package brix.web.picker.node;

import brix.jcr.wrapper.BrixNode;
import brix.web.tree.NodeFilter;

public class FileNodeFilter implements NodeFilter
{

    public static final FileNodeFilter INSTANCE = new FileNodeFilter();

	public boolean isNodeAllowed(BrixNode node)
	{		
		return node != null && !node.isFolder();
	}
}
