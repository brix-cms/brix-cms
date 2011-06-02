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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;

interface RemoteWorkspace extends Remote {
    public void delete() throws RemoteException;

    public String getAttribute(String attributeKey) throws RemoteException;

    public Iterator<String> getAttributeKeys() throws RemoteException;

    public String getId() throws RemoteException;

    public void setAttribute(String attributeKey, String attributeValue) throws RemoteException;
}
