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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerWorkspaceManager implements RemoteWorkspaceManager {
    private final WorkspaceManager delegate;

    private static List<RemoteWorkspace> localToRemote(List<Workspace> local)
            throws RemoteException {
        ArrayList<RemoteWorkspace> remote = new ArrayList<RemoteWorkspace>(local.size());
        for (Workspace workspace : local) {
            remote.add(new ServerWorkspace(workspace));
        }
        return remote;
    }

    public ServerWorkspaceManager(WorkspaceManager delegate) {
        this.delegate = delegate;
    }


    public RemoteWorkspace createWorkspace() throws RemoteException {
        return new ServerWorkspace(delegate.createWorkspace());
    }

    public RemoteWorkspace getWorkspace(String workspaceId) throws RemoteException {
        return new ServerWorkspace(delegate.getWorkspace(workspaceId));
    }

    public List<RemoteWorkspace> getWorkspaces() throws RemoteException {
        return localToRemote(delegate.getWorkspaces());
    }

    public List<RemoteWorkspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes)
            throws RemoteException {
        return localToRemote(delegate.getWorkspacesFiltered(workspaceAttributes));
    }

    public boolean workspaceExists(String workspaceId) throws RemoteException {
        return delegate.workspaceExists(workspaceId);
    }
}
