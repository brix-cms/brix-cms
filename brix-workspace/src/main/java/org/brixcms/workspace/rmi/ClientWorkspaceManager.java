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

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientWorkspaceManager implements WorkspaceManager {
    private RemoteWorkspaceManager delegate;
    private String url;

    private static List<Workspace> remoteToLocal(List<RemoteWorkspace> remote) {
        ArrayList<Workspace> local = new ArrayList<Workspace>(remote.size());
        for (RemoteWorkspace workspace : remote) {
            local.add(new ClientWorkspace(workspace));
        }
        return local;
    }

    public ClientWorkspaceManager(String url) {
        this.url = url;
    }

    public ClientWorkspaceManager(RemoteWorkspaceManager delegate) {
        this.delegate = delegate;
    }

    public RemoteWorkspaceManager getDelegate() {
        if (delegate == null) {
            delegate = lookup(url);
        }
        return delegate;
    }

    private static RemoteWorkspaceManager lookup(String url) {
        try {
            return (RemoteWorkspaceManager) Naming.lookup(url);
        } catch (Exception e) {
            throw new RuntimeException("Could not connect to remote url: " + url, e);
        }
    }



    public List<Workspace> getWorkspaces() {
        try {
            return remoteToLocal(getDelegate().getWorkspaces());
        } catch (RemoteException e) {
            delegate = null;
            throw new CommunicationException(e);
        }
    }

    public List<Workspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes) {
        try {
            return remoteToLocal(getDelegate().getWorkspacesFiltered(workspaceAttributes));
        } catch (RemoteException e) {
            delegate = null;
            throw new CommunicationException(e);
        }
    }

    public Workspace createWorkspace() {
        try {
            return new ClientWorkspace(getDelegate().createWorkspace());
        } catch (RemoteException e) {
            delegate = null;
            throw new CommunicationException(e);
        }
    }

    public Workspace getWorkspace(String workspaceId) {
        return new ClientWorkspace(workspaceId, getDelegate());
    }

    public boolean workspaceExists(String workspaceId) {
        try {
            return getDelegate().workspaceExists(workspaceId);
        } catch (RemoteException e) {
            delegate = null;
            throw new CommunicationException(e);
        }
    }
}
