package brix.workspace.rmi;

import java.rmi.RemoteException;
import java.util.Iterator;

import brix.workspace.Workspace;

public class ClientWorkspace implements Workspace
{
    private final RemoteWorkspace delegate;

    public ClientWorkspace(RemoteWorkspace delegate)
    {
        this.delegate = delegate;
    }

    public void delete()
    {
        try
        {
            delegate.delete();
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
            return delegate.getAttribute(attributeKey);
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
            return delegate.getAttributeKeys();
        }
        catch (RemoteException e)
        {
            throw new CommunicationException(e);
        }
    }

    public String getId()
    {
        try
        {
            return delegate.getId();
        }
        catch (RemoteException e)
        {
            throw new CommunicationException(e);
        }

    }

    public void setAttribute(String attributeKey, String attributeValue)
    {
        try
        {
            delegate.setAttribute(attributeKey, attributeValue);
        }
        catch (RemoteException e)
        {
            throw new CommunicationException(e);
        }

    }
}
