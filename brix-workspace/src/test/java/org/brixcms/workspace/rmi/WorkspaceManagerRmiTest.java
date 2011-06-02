/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.workspace.rmi;

import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceManager;
import org.easymock.classextension.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;


// FIXME figure out why this is failing on teamcity
@Ignore
public class WorkspaceManagerRmiTest {
    @Test
    public void test() throws Exception {
        WorkspaceManager remote = EasyMock.createMock(WorkspaceManager.class);

        Workspace workspace = EasyMock.createMock(Workspace.class);

        EasyMock.expect(remote.createWorkspace()).andReturn(workspace);
        workspace.delete();

        EasyMock.replay(remote, workspace);

        Registry registry = LocateRegistry.createRegistry(10000);

        ServerWorkspaceManager server = new ServerWorkspaceManager(remote);
        RemoteStub stub = UnicastRemoteObject.exportObject(server);
        registry.rebind("wm", stub);

        RemoteWorkspaceManager client = (RemoteWorkspaceManager) registry.lookup("wm");
        WorkspaceManager local = new ClientWorkspaceManager(client);

        Workspace w = local.createWorkspace();
        w.delete();
        EasyMock.verify(remote, workspace);

        UnicastRemoteObject.unexportObject(server, true);
    }
}
