package brix.workspace;

import org.apache.wicket.model.IModel;

import brix.Brix;

public class WorkspaceModel implements IModel<Workspace>
{
    public WorkspaceModel(String workspaceId)
    {        
        this.workspaceId = workspaceId;
    }
    
    public WorkspaceModel(Workspace workspace)
    {
        if (workspace != null)
        {
        	setObject(workspace);   
        }        
    }
    
    public void setObject(Workspace workspace)
    {
        if (workspace != null)
        {
        	this.workspaceId = workspace.getId();    
        }
        else
        {
        	this.workspaceId = null;
        }
        this.workspace = workspace;
    }
    
    public Workspace getObject()
    {
        if (workspace == null && workspaceId != null)
        {
            workspace = Brix.get().getWorkspaceManager().getWorkspace(workspaceId);
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
