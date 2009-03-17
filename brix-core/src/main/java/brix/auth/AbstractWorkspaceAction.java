package brix.auth;

import brix.workspace.Workspace;


public abstract class AbstractWorkspaceAction extends AbstractAction
{    
    private final Workspace workspace;

    public AbstractWorkspaceAction(Context context, Workspace workspace)
    {
        super(context);
        this.workspace = workspace;
    }

    public Workspace getWorkspace()
    {
        return workspace;
    }

    @Override
    public String toString()
    {
        return "AbstractWorkspaceAction{" + "workspace=" + workspace + "} " + super.toString();
    }
}
