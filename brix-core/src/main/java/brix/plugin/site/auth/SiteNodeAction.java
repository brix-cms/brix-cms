package brix.plugin.site.auth;

import brix.auth.AbstractNodeAction;
import brix.jcr.api.JcrNode;

public class SiteNodeAction extends AbstractNodeAction
{
    public enum Type {
        NODE_VIEW,
        NODE_VIEW_CHILDREN,
        NODE_ADD_CHILD,
        NODE_EDIT,
        NODE_DELETE,
        NODE_RENAME,
    };

    private final Type type;

    public SiteNodeAction(Context context, Type type, JcrNode node)
    {
        super(context, node);
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }
}
