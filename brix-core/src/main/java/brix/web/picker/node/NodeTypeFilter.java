package brix.web.picker.node;

import brix.jcr.wrapper.BrixNode;
import brix.web.tree.NodeFilter;

public class NodeTypeFilter implements NodeFilter
{

	private final String nodeTypes[];

	public NodeTypeFilter(String nodeType)
	{
		if (nodeType == null)
		{
			throw new IllegalArgumentException("Argument 'nodeType' may not be null.");
		}
		this.nodeTypes = new String[] { nodeType };
	}

	public NodeTypeFilter(String... nodeTypes)
	{
		if (nodeTypes == null)
		{
			throw new IllegalArgumentException("Argument 'nodeTypes' may not be null.");
		}
		for (String s : nodeTypes)
		{
			if (s == null)
			{
				throw new IllegalArgumentException("Argument 'nodeTypes' may not contain null value.");
			}
		}
		this.nodeTypes = nodeTypes;
	}

	public boolean isNodeAllowed(BrixNode node)
	{
		for (String type : nodeTypes)
		{
			if (node != null && type.equals(node.getNodeType()))
				return true;
		}
		return false;
	}
}
