package brix.auth;

import brix.jcr.api.JcrNode;

public interface NodeAction extends Action
{

    public enum Type {
        NODE_VIEW,
        NODE_VIEW_CHILDREN,
        NODE_ADD_CHILD,
        NODE_EDIT,
        NODE_DELETE,
        NODE_RENAME,
    };

    public Type getType();

    public JcrNode getNode();

}
