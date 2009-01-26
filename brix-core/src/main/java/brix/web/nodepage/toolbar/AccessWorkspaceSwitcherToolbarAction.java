package brix.web.nodepage.toolbar;

import brix.auth.Action;

/**
 * Action that determines whether or not a user can see the workspace switching toolbar when
 * viewing a site.
 * 
 * @author igor.vaynberg
 * 
 */
public class AccessWorkspaceSwitcherToolbarAction implements Action
{

    public Context getContext()
    {
        return Context.PRESENTATION;
    }

}
