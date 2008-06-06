package brix.plugin.site.auth;

import brix.auth.AbstractNodeAction;
import brix.jcr.wrapper.BrixNode;

public class ConvertNodeAction extends AbstractNodeAction
{
    private final String targetType;

    public ConvertNodeAction(Context context, BrixNode node, String targetType)
    {
        super(context, node);
        this.targetType = targetType;
    }

    public String getTargetNodeType()
    {
        return targetType;
    }

}
