package brix.workspace.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;

import org.easymock.classextension.EasyMock;
import org.junit.Test;

import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class TestRmi
{
    @Test
    public void test() throws Exception
    {

        WorkspaceManager remote = EasyMock.createMock(WorkspaceManager.class);

        Workspace workspace = EasyMock.createMock(Workspace.class);

        EasyMock.expect(remote.createWorkspace()).andReturn(workspace);

        EasyMock.replay(remote, workspace);

        Registry registry = LocateRegistry.getRegistry();

        ServerWorkspaceManager server = new ServerWorkspaceManager(remote);
        RemoteStub stub = UnicastRemoteObject.exportObject(server);
        registry.rebind("wm", stub);

        ServerWorkspaceManager client = (ServerWorkspaceManager)registry.lookup("wm");
        WorkspaceManager local = new ClientWorkspaceManager(client);
        Workspace w = local.createWorkspace();

        EasyMock.verify(server, workspace);


    }
}
