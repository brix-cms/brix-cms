package brix.plugin.snapshot.auth;

import brix.auth.AbstractWorkspaceAction;
import brix.workspace.Workspace;

public class RestoreSnapshotAction extends AbstractWorkspaceAction
{
    private final Workspace snapshotWorkspace;

    public RestoreSnapshotAction(Context context, Workspace targetWorkspace, Workspace snapshotWorkspace)
    {
        super(context, targetWorkspace);
        this.snapshotWorkspace = snapshotWorkspace;
    }
    
    public RestoreSnapshotAction(Context context, Workspace targetWorkspace)
    {
        super(context, targetWorkspace);
        this.snapshotWorkspace = null;
    }
    
    public boolean isFromXML()
    {
        return snapshotWorkspace == null;
    }
    
    public Workspace getSnapshotWorkspace()
    {
        return snapshotWorkspace;
    }
    
    public Workspace getTargetWorkspace()
    {
        return getWorkspace();
    }

}
