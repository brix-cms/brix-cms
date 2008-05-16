package brix.workspace.rmi;

import java.rmi.RemoteException;
import java.util.Iterator;

import brix.workspace.Workspace;

public class ServerWorkspace implements RemoteWorkspace
{
    private final Workspace delegate;

    public ServerWorkspace(Workspace delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void delete() throws RemoteException
    {
        delegate.delete();
    }

    public String getAttribute(String attributeKey) throws RemoteException
    {
        return delegate.getAttribute(attributeKey);
    }

    public Iterator<String> getAttributeKeys() throws RemoteException
    {
        return delegate.getAttributeKeys();
    }

    public String getId() throws RemoteException
    {
        return delegate.getId();
    }

    public void setAttribute(String attributeKey, String attributeValue) throws RemoteException
    {
        delegate.setAttribute(attributeKey, attributeValue);
    }

}
