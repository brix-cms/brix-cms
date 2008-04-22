package brix.auth.impl;

import brix.auth.WorkspaceAction;

public class WorkspaceActionImpl implements WorkspaceAction
{

    private final Context context;
    private final Type type;
    private final String workspaceName;

    public WorkspaceActionImpl(Context context, Type type, String workspaceName)
    {
        this.context = context;
        this.type = type;
        this.workspaceName = workspaceName;
    }

    public Context getContext()
    {
        return context;
    }

    public Type getType()
    {
        return type;
    }

    public String getWorkspaceName()
    {
        return workspaceName;
    }

}
