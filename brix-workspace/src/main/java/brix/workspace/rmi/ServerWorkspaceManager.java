package brix.workspace.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class ServerWorkspaceManager implements RemoteWorkspaceManager
{
    private final WorkspaceManager delegate;

    public ServerWorkspaceManager(WorkspaceManager delegate)
    {
        this.delegate = delegate;
    }

    public RemoteWorkspace createWorkspace() throws RemoteException
    {
        return new ServerWorkspace(delegate.createWorkspace());
    }

    public RemoteWorkspace getWorkspace(String workspaceId) throws RemoteException
    {
        return new ServerWorkspace(delegate.getWorkspace(workspaceId));
    }

    public List<RemoteWorkspace> getWorkspaces() throws RemoteException
    {
        return localToRemote(delegate.getWorkspaces());
    }

    public List<RemoteWorkspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes)
            throws RemoteException
    {
        return localToRemote(delegate.getWorkspacesFiltered(workspaceAttributes));
    }
    
    public boolean workspaceExists(String workspaceId) throws RemoteException
    {
    	return delegate.workspaceExists(workspaceId);
    }

    private static List<RemoteWorkspace> localToRemote(List<Workspace> local)
            throws RemoteException
    {
        ArrayList<RemoteWorkspace> remote = new ArrayList<RemoteWorkspace>(local.size());
        for (Workspace workspace : local)
        {
            remote.add(new ServerWorkspace(workspace));
        }
        return remote;
    }

}
