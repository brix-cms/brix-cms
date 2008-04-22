package brix.auth.impl;

import brix.auth.ConvertNodeAction;
import brix.jcr.api.JcrNode;

public class ConvertNodeActionImpl implements ConvertNodeAction
{

    private final Context context;
    private final JcrNode node;
    private final String targetType;

    public ConvertNodeActionImpl(Context context, JcrNode node, String targetType)
    {
        this.context = context;
        this.node = node;
        this.targetType = targetType;
    }

    public Context getContext()
    {
        return context;
    }

    public JcrNode getNode()
    {
        return node;
    }

    public String getTargetNodeType()
    {
        return targetType;
    }

}
