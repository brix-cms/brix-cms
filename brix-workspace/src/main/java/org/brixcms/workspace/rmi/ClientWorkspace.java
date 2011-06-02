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

import java.rmi.RemoteException;
import java.util.Iterator;

// TODO: Make it a bit smarter. The RMI communication should be much coarser grained
// Workspaces objects are short lived, we don't need fresh property values every time
// we query for them. All properties should be copied from server on workspace initialization
// (in 1 rmi call)
class ClientWorkspace implements Workspace {
    private RemoteWorkspace delegate;

    private String id;
    private RemoteWorkspaceManager remoteWorkspaceManager;

    public ClientWorkspace(RemoteWorkspace delegate) {
        this.delegate = delegate;
    }

    public ClientWorkspace(String id, RemoteWorkspaceManager manager) {
        this.id = id;
        this.remoteWorkspaceManager = manager;
    }

    public RemoteWorkspace getDelegate() {
        if (delegate == null) {
            try {
                delegate = remoteWorkspaceManager.getWorkspace(id);
            } catch (RemoteException e) {
                throw new CommunicationException(e);
            }
        }
        return delegate;
    }

    public String getId() {
        if (id == null) {
            try {
                return id = getDelegate().getId();
            } catch (RemoteException e) {
                throw new CommunicationException(e);
            }
        }
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ClientWorkspace == false) {
            return false;
        }
        ClientWorkspace that = (ClientWorkspace) obj;
        if (id == that.id) {
            return true;
        }
        if (id == null || that.id == null) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }



    public void setAttribute(String attributeKey, String attributeValue) {
        try {
            getDelegate().setAttribute(attributeKey, attributeValue);
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    public String getAttribute(String attributeKey) {
        try {
            return getDelegate().getAttribute(attributeKey);
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    public Iterator<String> getAttributeKeys() {
        try {
            return getDelegate().getAttributeKeys();
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    public void delete() {
        try {
            getDelegate().delete();
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }
}
