package brix.auth;

import brix.jcr.api.JcrNode;

public interface ConvertNodeAction extends Action
{

    public JcrNode getNode();

    public String getTargetNodeType();
}
