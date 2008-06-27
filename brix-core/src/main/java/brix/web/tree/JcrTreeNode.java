package brix.web.tree;

import java.util.List;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

public interface JcrTreeNode extends TreeNode
{    
	/**
	 * Returns {@link BrixNode} associated with this tree node. Note that
	 * it is valid for this method to return null.
	 * 
	 * @return
	 */
    public IModel<BrixNode> getNodeModel();
    
    public List<? extends JcrTreeNode> getChildren();
}
