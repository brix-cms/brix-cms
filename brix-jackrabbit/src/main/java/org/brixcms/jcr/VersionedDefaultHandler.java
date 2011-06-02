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

package org.brixcms.jcr;

import org.apache.jackrabbit.server.io.DefaultHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.io.IOException;

public class VersionedDefaultHandler extends DefaultHandler {
    public VersionedDefaultHandler() {
    }

    public VersionedDefaultHandler(IOManager ioManager) {
        super(ioManager);
    }

    public VersionedDefaultHandler(IOManager ioManager, String collectionNodetype,
                                   String defaultNodetype, String contentNodetype) {
        super(ioManager, collectionNodetype, defaultNodetype, contentNodetype);
    }


    @Override
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
        if (!canImport(context, isCollection)) {
            throw new IOException(getName() + ": Cannot import " + context.getSystemId());
        }

        try {
            Node node = getNode(context, isCollection);

            boolean needToCheckIn = false;
            VersionManager vm = node.getSession().getWorkspace().getVersionManager();

            if (node instanceof Version && node.isCheckedOut() == false) {
                vm.checkout(node.getPath());
                needToCheckIn = true;
            }

            boolean result = super.importContent(context, isCollection);

            if (needToCheckIn) {
                node.getSession().save();
                vm.checkin(node.getPath());
            }

            return result;
        } catch (RepositoryException e) {
            throw new IOException(e.getMessage());
        }
    }

    private Node getNode(ImportContext context, boolean isCollection) throws RepositoryException {
        Node parentNode = (Node) context.getImportRoot();
        String name = context.getSystemId();
        if (parentNode.hasNode(name)) {
            parentNode = parentNode.getNode(name);
        } else {
            String ntName = (isCollection) ? getCollectionNodeType() : getNodeType();
            parentNode = parentNode.addNode(name, ntName);
        }
        return parentNode;
    }
}
