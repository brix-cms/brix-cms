package brix.web.tree;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

public interface JcrTreeNode extends TreeNode
{
    
    public IModel<BrixNode> getNodeModel();
    
}
