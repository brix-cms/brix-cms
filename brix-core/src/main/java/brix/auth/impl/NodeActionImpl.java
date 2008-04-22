package brix.auth.impl;

import brix.auth.NodeAction;
import brix.jcr.api.JcrNode;

public class NodeActionImpl implements NodeAction
{

    private final JcrNode node;
    private final Type type;
    private final Context context;

    public NodeActionImpl(Context context, Type type, JcrNode node)
    {
        this.context = context;
        this.type = type;
        this.node = node;
    }

    public JcrNode getNode()
    {
        return node;
    }

    public Type getType()
    {
        return type;
    }

    public Context getContext()
    {
        return context;
    }

}
