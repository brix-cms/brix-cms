package brix.demo.web;

import brix.auth.Action;
import brix.auth.AuthorizationStrategy;
import brix.plugin.site.auth.SiteNodeAction;

public class DemoAuthorizationStrategy implements AuthorizationStrategy
{

    private boolean isActionAuthorized(SiteNodeAction action)
    {
        if (action.getType() == SiteNodeAction.Type.NODE_VIEW_CHILDREN &&
                action.getNode().getName().equals("content"))
            return false;
        else
            return true;
    }

    public boolean isActionAuthorized(Action action)
    {
        if (action instanceof SiteNodeAction)
        {
            return isActionAuthorized((SiteNodeAction)action);
        }
        return true;
    }

}
