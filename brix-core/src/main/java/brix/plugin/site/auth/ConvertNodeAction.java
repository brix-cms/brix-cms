package brix.plugin.site.auth;

import brix.auth.AbstractNodeAction;
import brix.jcr.api.JcrNode;

public class ConvertNodeAction extends AbstractNodeAction
{
    private final String targetType;

    public ConvertNodeAction(Context context, JcrNode node, String targetType)
    {
        super(context, node);
        this.targetType = targetType;
    }

    public String getTargetNodeType()
    {
        return targetType;
    }

}
