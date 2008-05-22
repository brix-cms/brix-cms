package brix.workspace.rmi;

import java.io.InputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;

import javax.jcr.nodetype.NodeType;

import brix.workspace.WorkspaceNodeTypeManager;

public class ClientWorkspaceNodeTypeManager implements WorkspaceNodeTypeManager
{
	private final RemoteWorkspaceNodeTypeManager server;

	public ClientWorkspaceNodeTypeManager(RemoteWorkspaceNodeTypeManager server)
	{
		this.server = server;
	}

	public ClientWorkspaceNodeTypeManager(String url)
	{
		this(lookup(url));
	}

	private static RemoteWorkspaceNodeTypeManager lookup(String url)
	{
		try
		{
			return (RemoteWorkspaceNodeTypeManager) Naming.lookup(url);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not connect to remote url: " + url, e);
		}
	}

	public void registerNodeTypes(String workspace, InputStream in, String contentType, boolean reregisterExisting)
	{
		try
		{
			server.registerNodeTypes(workspace, in, contentType, reregisterExisting);
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
	}

}
