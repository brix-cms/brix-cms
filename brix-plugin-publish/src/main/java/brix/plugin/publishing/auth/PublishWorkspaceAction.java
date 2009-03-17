package brix.plugin.publishing.auth;

import brix.auth.AbstractWorkspaceAction;
import brix.workspace.Workspace;

public class PublishWorkspaceAction extends AbstractWorkspaceAction
{
    public PublishWorkspaceAction(Context context, Workspace workspace, String targetState)
    {
        super(context, workspace);
        this.targetState = targetState;
    }

    private final String targetState;

    public String getTargetState()
    {
        return targetState;
    }

    @Override
    public String toString()
    {
        return "PublishWorkspaceAction{" + "targetState='" + targetState + '\'' + "} " + super.toString();
    }
}
