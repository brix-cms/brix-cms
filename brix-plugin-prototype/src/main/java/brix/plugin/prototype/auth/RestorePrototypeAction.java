package brix.plugin.prototype.auth;

import brix.auth.AbstractWorkspaceAction;
import brix.workspace.Workspace;

public class RestorePrototypeAction extends AbstractWorkspaceAction
{
    private final Workspace templateWorkspace;

    public RestorePrototypeAction(Context context, Workspace targetWorkspace, Workspace templateWorkspace)
    {
        super(context, targetWorkspace);
        this.templateWorkspace = templateWorkspace;
    }
    
    public Workspace getTemplateWorkspace()
    {
        return templateWorkspace;
    }
    
    public Workspace getTargetWorkspace()
    {
        return getWorkspace();
    }

    @Override
    public String toString()
    {
        return "RestorePrototypeAction{" + "templateWorkspace=" + templateWorkspace + "} " + super.toString();
    }
}
