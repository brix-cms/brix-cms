package brix.auth.impl;

import brix.auth.SelectNewNodeTypeAction;
import brix.jcr.api.JcrNode;

public class SelectNewNodeTypeActionImpl implements SelectNewNodeTypeAction
{

    private final String nodeType;
    private final JcrNode parentNode;
    private final Context context;

    public SelectNewNodeTypeActionImpl(Context context, JcrNode parentNode, String nodeType)
    {
        this.context = context;
        this.parentNode = parentNode;
        this.nodeType = nodeType;
    }

    public String getNodeType()
    {
        return nodeType;
    }

    public JcrNode getParentNode()
    {
        return parentNode;
    }

    public Context getContext()
    {
        return context;
    }

}
