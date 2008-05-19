package brix.plugin.template.auth;

import brix.auth.AbstractWorkspaceAction;
import brix.workspace.Workspace;

public class RestoreTemplateAction extends AbstractWorkspaceAction
{
    private final Workspace templateWorkspace;

    public RestoreTemplateAction(Context context, Workspace targetWorkspace, Workspace templateWorkspace)
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

}
