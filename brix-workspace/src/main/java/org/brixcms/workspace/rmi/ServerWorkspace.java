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
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;

class ServerWorkspace extends UnicastRemoteObject implements RemoteWorkspace {
    private final Workspace delegate;

    public ServerWorkspace(Workspace delegate) throws RemoteException {
        this.delegate = delegate;
    }


    public void delete() throws RemoteException {
        delegate.delete();
    }

    public String getAttribute(String attributeKey) throws RemoteException {
        return delegate.getAttribute(attributeKey);
    }

    public Iterator<String> getAttributeKeys() throws RemoteException {
        return delegate.getAttributeKeys();
    }

    public String getId() throws RemoteException {
        return delegate.getId();
    }

    public void setAttribute(String attributeKey, String attributeValue) throws RemoteException {
        delegate.setAttribute(attributeKey, attributeValue);
    }
}
