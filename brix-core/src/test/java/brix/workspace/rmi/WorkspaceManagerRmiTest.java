package brix.workspace.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;

import org.easymock.classextension.EasyMock;
import org.junit.Test;

import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class WorkspaceManagerRmiTest
{
    @Test
    public void test() throws Exception
    {

        WorkspaceManager remote = EasyMock.createMock(WorkspaceManager.class);

        Workspace workspace = EasyMock.createMock(Workspace.class);

        EasyMock.expect(remote.createWorkspace()).andReturn(workspace);
        workspace.delete();

        EasyMock.replay(remote, workspace);

        Registry registry = LocateRegistry.createRegistry(10000);

        ServerWorkspaceManager server = new ServerWorkspaceManager(remote);
        RemoteStub stub = UnicastRemoteObject.exportObject(server);
        registry.rebind("wm", stub);

        RemoteWorkspaceManager client = (RemoteWorkspaceManager)registry.lookup("wm");
        WorkspaceManager local = new ClientWorkspaceManager(client);

        Workspace w = local.createWorkspace();
        w.delete();
        EasyMock.verify(remote, workspace);

        UnicastRemoteObject.unexportObject(server, true);

    }
}
