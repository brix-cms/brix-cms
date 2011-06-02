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

package org.brixcms.jcr.base.action;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.lock.Lock;
import javax.jcr.version.Version;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CompoundActionHandler extends AbstractActionHandler {
    private final List<AbstractActionHandler> handlers = new ArrayList<AbstractActionHandler>();

    public void addHandler(AbstractActionHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Argument 'handler' may not be null.");
        }
        handlers.add(handler);
        sort();
    }

    private void sort() {
        Collections.sort(handlers, new Comparator<AbstractActionHandler>() {
            public int compare(AbstractActionHandler o1, AbstractActionHandler o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
    }

    @Override
    public void afterItemRefresh(Item item, boolean keepChanges) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterItemRefresh(item, keepChanges);
        }
    }

    @Override
    public void afterItemSave(Item item) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterItemSave(item);
        }
    }

    @Override
    public void afterNodeAdd(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeAdd(node);
        }
    }

    @Override
    public void afterNodeAddMixin(Node node, String mixin) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeAddMixin(node, mixin);
        }
    }

    @Override
    public void afterNodeCancelMerge(Node node, Version version) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeCancelMerge(node, version);
        }
    }

    @Override
    public void afterNodeCheckin(Node node, Version version) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeCheckin(node, version);
        }
    }

    @Override
    public void afterNodeCheckout(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeCheckout(node);
        }
    }

    @Override
    public void afterNodeChildNodesOrderChange(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeChildNodesOrderChange(node);
        }
    }

    @Override
    public void afterNodeDoneMerge(Node node, Version version) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeDoneMerge(node, version);
        }
    }

    @Override
    public void afterNodeLock(Node node, boolean isDeep, boolean isSessionScoped, Lock lock) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeLock(node, isDeep, isSessionScoped, lock);
        }
    }

    @Override
    public void afterNodeRemoveMixin(Node node, String mixin) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeRemoveMixin(node, mixin);
        }
    }

    @Override
    public void afterNodeRestoreVersion(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeRestoreVersion(node);
        }
    }

    @Override
    public void afterNodeUnlock(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeUnlock(node);
        }
    }

    @Override
    public void afterNodeUpdate(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterNodeUpdate(node);
        }
    }

    @Override
    public void afterPropertyRemove(Node node, String propertyName) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterPropertyRemove(node, propertyName);
        }
    }

    @Override
    public void afterPropertySet(Property property) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterPropertySet(property);
        }
    }

    @Override
    public void afterSessionImportXML(String parentAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterSessionImportXML(parentAbsPath);
        }
    }

    @Override
    public void afterSessionNodeMove(String sourcePath, String destinationPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterSessionNodeMove(sourcePath, destinationPath);
        }
    }

    @Override
    public void afterSessionRefresh(boolean keepChanges) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterSessionRefresh(keepChanges);
        }
    }

    @Override
    public void afterSessionSave() throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterSessionSave();
        }
    }

    @Override
    public void afterWorkspaceClone(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterWorkspaceClone(srcWorkspace, srcAbsPath, destAbsPath);
        }
    }

    @Override
    public void afterWorkspaceCopy(String srcAbsPath, String destAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterWorkspaceCopy(srcAbsPath, destAbsPath);
        }
    }

    @Override
    public void afterWorkspaceCopy(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterWorkspaceCopy(srcWorkspace, srcAbsPath, destAbsPath);
        }
    }

    @Override
    public void afterWorkspaceImportXML(String parentAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterWorkspaceImportXML(parentAbsPath);
        }
    }

    @Override
    public void afterWorkspaceMove(String srcAbsPath, String destAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.afterWorkspaceMove(srcAbsPath, destAbsPath);
        }
    }

    @Override
    public void beforeItemRefresh(Item item, boolean keepChanges) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeItemRefresh(item, keepChanges);
        }
    }

    @Override
    public void beforeItemSave(Item item) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeItemSave(item);
        }
    }

    @Override
    public void beforeNodeAdd(Node parent, String name, String primaryType) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeAdd(parent, name, primaryType);
        }
    }

    @Override
    public void beforeNodeAddMixin(Node node, String mixin) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeAddMixin(node, mixin);
        }
    }

    @Override
    public void beforeNodeCancelMerge(Node node, Version version) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeCancelMerge(node, version);
        }
    }

    @Override
    public void beforeNodeCheckin(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeCheckin(node);
        }
    }

    @Override
    public void beforeNodeCheckout(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeCheckout(node);
        }
    }

    @Override
    public void beforeNodeChildNodesOrderChange(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeChildNodesOrderChange(node);
        }
    }

    @Override
    public void beforeNodeDoneMerge(Node node, Version version) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeDoneMerge(node, version);
        }
    }

    @Override
    public void beforeNodeLock(Node node, boolean isDeep, boolean isSessionScoped) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeLock(node, isDeep, isSessionScoped);
        }
    }

    @Override
    public void beforeNodeRemove(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeRemove(node);
        }
    }

    @Override
    public void beforeNodeRemoveMixin(Node node, String mixin) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeRemoveMixin(node, mixin);
        }
    }

    @Override
    public void beforeNodeRestoreVersion(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeRestoreVersion(node);
        }
    }

    @Override
    public void beforeNodeUnlock(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeUnlock(node);
        }
    }

    @Override
    public void beforeNodeUpdate(Node node) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeNodeUpdate(node);
        }
    }

    @Override
    public void beforePropertyRemove(Node node, String propertyName) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforePropertyRemove(node, propertyName);
        }
    }

    @Override
    public void beforePropertySet(Node node, String propertyName) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforePropertySet(node, propertyName);
        }
    }

    @Override
    public void beforeSessionImportXML(String parentAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeSessionImportXML(parentAbsPath);
        }
    }

    @Override
    public void beforeSessionNodeMove(String sourcePath, String destinationPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeSessionNodeMove(sourcePath, destinationPath);
        }
    }

    @Override
    public void beforeSessionRefresh(boolean keepChanges) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeSessionRefresh(keepChanges);
        }
    }

    @Override
    public void beforeSessionSave() throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeSessionSave();
        }
    }

    @Override
    public void beforeWorkspaceClone(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeWorkspaceClone(srcWorkspace, srcAbsPath, destAbsPath);
        }
    }

    @Override
    public void beforeWorkspaceCopy(String srcAbsPath, String destAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeWorkspaceCopy(srcAbsPath, destAbsPath);
        }
    }

    @Override
    public void beforeWorkspaceCopy(String srcWorkspace, String srcAbsPath, String destAbsPath)
            throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeWorkspaceCopy(srcWorkspace, srcAbsPath, destAbsPath);
        }
    }

    @Override
    public void beforeWorkspaceImportXML(String parentAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeWorkspaceImportXML(parentAbsPath);
        }
    }

    @Override
    public void beforeWorkspaceMove(String srcAbsPath, String destAbsPath) throws RepositoryException {
        for (AbstractActionHandler handler : handlers) {
            handler.beforeWorkspaceMove(srcAbsPath, destAbsPath);
        }
    }

    public void removeHAndler(AbstractActionHandler handler) {
        handlers.remove(handler);
    }
}
