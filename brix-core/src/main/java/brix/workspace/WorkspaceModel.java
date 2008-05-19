package brix.workspace;

import org.apache.wicket.model.IModel;

import brix.BrixRequestCycle;

public class WorkspaceModel implements IModel<Workspace>
{
    public WorkspaceModel(String workspaceId)
    {
        if (workspaceId == null)
        {
            throw new IllegalArgumentException("Argument 'workspaceId' can not be null.");
        }
        this.workspaceId = workspaceId;
    }
    
    public WorkspaceModel(Workspace workspace)
    {
        if (workspace == null)
        {
            throw new IllegalArgumentException("Argument 'workspace' can not be null.");
        }
        setObject(workspace);
    }
    
    public void setObject(Workspace workspace)
    {
        if (workspace == null)
        {
            throw new IllegalArgumentException("Argument 'workspace' can not be null.");
        }
        this.workspaceId = workspace.getId();
        this.workspace = workspace;
    }
    
    public Workspace getObject()
    {
        if (workspace == null)
        {
            workspace = BrixRequestCycle.Locator.getBrix().getWorkspaceManager().getWorkspace(workspaceId);
        }
        return workspace;
    }
    
    private String workspaceId;
    private transient Workspace workspace;
    
    public void detach()
    {
        workspace = null;
    }
    
}
