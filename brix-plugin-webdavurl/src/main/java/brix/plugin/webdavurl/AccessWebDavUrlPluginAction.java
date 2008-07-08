package brix.plugin.webdavurl;

import brix.auth.Action;
import brix.workspace.Workspace;

public class AccessWebDavUrlPluginAction implements Action
{
    private final Workspace workspace;

    public AccessWebDavUrlPluginAction(Workspace workspace)
    {
        this.workspace = workspace;
    }


    public Workspace getWorkspace()
    {
        return workspace;
    }


    public Context getContext()
    {
        return Context.ADMINISTRATION;
    }

}
