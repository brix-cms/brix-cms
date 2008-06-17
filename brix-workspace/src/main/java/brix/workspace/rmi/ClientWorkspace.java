package brix.workspace.rmi;

import java.rmi.RemoteException;
import java.util.Iterator;

import brix.workspace.Workspace;

// TODO: Make it a bit smarter. The RMI communication should be much coarser grained
// Workspaces objects are short lived, we don't need fresh property values every time
// we query for them. All properties should be copied from server on workspace initialization
// (in 1 rmi call)
class ClientWorkspace implements Workspace
{
	private RemoteWorkspace delegate;

	private String id;
	private RemoteWorkspaceManager remoteWorkspaceManager;

	public ClientWorkspace(String id, RemoteWorkspaceManager manager)
	{
		this.id = id;
		this.remoteWorkspaceManager = manager;
	}

	public ClientWorkspace(RemoteWorkspace delegate)
	{
		this.delegate = delegate;
	}

	public RemoteWorkspace getDelegate()
	{
		if (delegate == null)
		{
			try
			{
				delegate = remoteWorkspaceManager.getWorkspace(id);
			}
			catch (RemoteException e)
			{
				throw new CommunicationException(e);
			}
		}
		return delegate;
	}

	public void delete()
	{
		try
		{
			getDelegate().delete();
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
	}

	public String getAttribute(String attributeKey)
	{
		try
		{
			return getDelegate().getAttribute(attributeKey);
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
	}

	public Iterator<String> getAttributeKeys()
	{
		try
		{
			return getDelegate().getAttributeKeys();
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}
	}

	public String getId()
	{
		if (id == null)
		{
			try
			{
				return id = getDelegate().getId();
			}
			catch (RemoteException e)
			{
				throw new CommunicationException(e);
			}
		}
		return id;
	}

	public void setAttribute(String attributeKey, String attributeValue)
	{
		try
		{
			getDelegate().setAttribute(attributeKey, attributeValue);
		}
		catch (RemoteException e)
		{
			throw new CommunicationException(e);
		}

	}
	
	@Override
	public int hashCode() 
	{
		return id != null ? id.hashCode() : 0;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
		{
			return true;
		}
		if (obj instanceof ClientWorkspace == false)
		{
			return false;
		}
		ClientWorkspace that = (ClientWorkspace) obj;
		if (id == that.id)
		{
			return true;
		}
		if (id == null || that.id == null)
		{
			return false;
		}
		return id.equals(that.id);
	}
	
	
}
