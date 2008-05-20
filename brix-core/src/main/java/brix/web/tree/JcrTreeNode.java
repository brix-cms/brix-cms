package brix.web.tree;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;

public interface JcrTreeNode extends TreeNode
{
    
    public IModel<JcrNode> getNodeModel();
    
}
