package brix.workspace.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class ClientWorkspaceManager implements WorkspaceManager
{
    private final RemoteWorkspaceManager delegate;


    public ClientWorkspaceManager(RemoteWorkspaceManager delegate)
    {
        this.delegate = delegate;
    }

    public Workspace createWorkspace()
    {
        try
        {        	
            return new ClientWorkspace(delegate.createWorkspace());
        }
        catch (RemoteException e)
        {
            throw new CommunicationException(e);
        }
    }

    public Workspace getWorkspace(String workspaceId)
    {
        return new ClientWorkspace(workspaceId, delegate);        
    }

    public List<Workspace> getWorkspaces()
    {
        try
        {
            return remoteToLocal(delegate.getWorkspaces());
        }
        catch (RemoteException e)
        {
            throw new CommunicationException(e);
        }

    }
    
    public boolean workspaceExists(String workspaceId)
    {
    	try
		{
			return delegate.workspaceExists(workspaceId);
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
    }

    public List<Workspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes)
    {
        try
        {
            return remoteToLocal(delegate.getWorkspacesFiltered(workspaceAttributes));
        }
        catch (RemoteException e)
        {
            throw new CommunicationException(e);
        }
    }

    private static List<Workspace> remoteToLocal(List<RemoteWorkspace> remote)
    {
        ArrayList<Workspace> local = new ArrayList<Workspace>(remote.size());
        for (RemoteWorkspace workspace : remote)
        {
            local.add(new ClientWorkspace(workspace));
        }
        return local;
    }
}
