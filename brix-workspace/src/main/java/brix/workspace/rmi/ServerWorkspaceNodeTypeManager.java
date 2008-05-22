package brix.workspace.rmi;

import java.io.InputStream;
import java.rmi.RemoteException;

import brix.workspace.WorkspaceNodeTypeManager;

public class ServerWorkspaceNodeTypeManager implements RemoteWorkspaceNodeTypeManager
{
	private final WorkspaceNodeTypeManager delegate;

	public ServerWorkspaceNodeTypeManager(WorkspaceNodeTypeManager delegate)
	{
		this.delegate = delegate;
	}

	public void registerNodeTypes(String workspace, InputStream in, String contentType, boolean reregisterExisting)
			throws RemoteException
	{
		delegate.registerNodeTypes(workspace, in, contentType, reregisterExisting);
	}

}
