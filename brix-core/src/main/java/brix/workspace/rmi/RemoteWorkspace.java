package brix.workspace.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;

public interface RemoteWorkspace extends Remote
{
    public String getId() throws RemoteException;

    public void setAttribute(String attributeKey, String attributeValue) throws RemoteException;

    public String getAttribute(String attributeKey) throws RemoteException;

    public Iterator<String> getAttributeKeys() throws RemoteException;

    public void delete() throws RemoteException;
}
