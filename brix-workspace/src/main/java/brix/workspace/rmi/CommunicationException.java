package brix.workspace.rmi;

import java.rmi.RemoteException;

public class CommunicationException extends RuntimeException
{
    public CommunicationException(RemoteException cause)
    {
        super("Error communicating with RMI server", cause);
    }

    {

    }
}
