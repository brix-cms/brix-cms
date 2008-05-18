package brix.workspace.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

interface RemoteWorkspaceManager extends Remote
{
    public List<RemoteWorkspace> getWorkspaces() throws RemoteException;

    public List<RemoteWorkspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes)
            throws RemoteException;

    public RemoteWorkspace createWorkspace() throws RemoteException;

    public RemoteWorkspace getWorkspace(String workspaceId) throws RemoteException;
}
