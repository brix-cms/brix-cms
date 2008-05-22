package brix.workspace.rmi;

import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteWorkspaceNodeTypeManager extends Remote
{
	public void registerNodeTypes(String workspace, InputStream in, String contentType, boolean reregisterExisting)
			throws RemoteException;
}
