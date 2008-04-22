package brix.web.admin;

import brix.auth.Action;
import brix.auth.AuthorizationStrategy;
import brix.auth.NodeAction;

public class DefaultAuthorizationStrategy implements AuthorizationStrategy
{

    private boolean isActionAuthorized(NodeAction action)
    {
        if (action.getType() == NodeAction.Type.NODE_VIEW_CHILDREN &&
                action.getNode().getName().equals("content"))
            return false;
        else
            return true;
    }

    public boolean isActionAuthorized(Action action)
    {
        if (action instanceof NodeAction)
        {
            return isActionAuthorized((NodeAction)action);
        }
        return true;
    }

}
