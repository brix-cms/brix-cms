package brix.auth;

import brix.jcr.api.JcrNode;

/**
 * Action used to filter the list of available node types upon node creation.
 * 
 * @author Matej Knopp
 */
public interface SelectNewNodeTypeAction extends Action
{
    public String getNodeType();

    public JcrNode getParentNode();
}
