package brix.web.picker.common;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.web.tree.FilteredJcrTreeNode;
import brix.web.tree.JcrTreeNode;
import brix.web.tree.NodeFilter;

/**
 * Interface from nodes that can provide {@link JcrTreeNode} for other nodes.
 * This is to create Tree structure for NodePicker. 
 * 
 * @author Matej Knopp
 */
public interface TreeAwareNode extends JcrNode
{

	public JcrTreeNode getTreeNode(BrixNode node);

	public static class Util
	{
		public static JcrTreeNode getTreeNode(BrixNode node, NodeFilter filter)
		{
			BrixNode n = node;
			while (n.getDepth() > 0)
			{
				if (n instanceof TreeAwareNode)
				{
					JcrTreeNode result = ((TreeAwareNode)n).getTreeNode(node);
					if (result != null)
					{
						if (filter != null)
						{
							result = new FilteredJcrTreeNode(result, filter);
						}
						return result;
					}					
				}	
				n = (BrixNode) n.getParent();
			}
			return null;
		}
		
		public static JcrTreeNode getTreeNode(BrixNode node)
		{
			return getTreeNode(node, null);
		}
	};
}
