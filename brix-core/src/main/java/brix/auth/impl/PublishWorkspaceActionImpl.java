package brix.auth.impl;

import brix.auth.WorkspaceAction;


public class PublishWorkspaceActionImpl extends WorkspaceActionImpl
{
    private final String targetState;

    public PublishWorkspaceActionImpl(Context context, String workspaceName,
            String targetState)
    {
        super(context, WorkspaceAction.Type.PUBLISH, workspaceName);
        this.targetState = targetState;
    }

    public String getTargetState()
    {
        return targetState;
    }
}
