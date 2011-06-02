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

package org.brixcms.jcr.base.wrapper;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;

abstract class ItemWrapper extends BaseWrapper<Item> implements Item {
    public static ItemWrapper wrap(Item item, SessionWrapper session) {
        if (item == null) {
            return null;
        } else if (item instanceof Version) {
            return VersionWrapper.wrap((Version) item, session);
        } else if (item instanceof VersionHistory) {
            return VersionHistoryWrapper.wrap((VersionHistory) item, session);
        } else if (item instanceof Node) {
            return NodeWrapper.wrap((Node) item, session);
        } else if (item instanceof Property) {
            return PropertyWrapper.wrap((Property) item, session);
        } else {
            throw new IllegalStateException("Unknown item subclass");
        }
    }

    protected ItemWrapper(Item delegate, SessionWrapper session) {
        super(delegate, session);
    }



    public String getPath() throws RepositoryException {
        // TODO: Cache
        return getDelegate().getPath();
    }

    public String getName() throws RepositoryException {
        // TODO: Cache
        return getDelegate().getName();
    }

    public Item getAncestor(int depth) throws RepositoryException {
        return getDelegate().getAncestor(depth);
    }

    public Node getParent() throws RepositoryException {
        return NodeWrapper.wrap(getDelegate().getParent(), getSessionWrapper());
    }

    public int getDepth() throws RepositoryException {
        return getDelegate().getDepth();
    }

    public Session getSession() throws RepositoryException {
        return getSessionWrapper();
    }

    public boolean isNew() {
        return getDelegate().isNew();
    }

    public boolean isModified() {
        return getDelegate().isModified();
    }

    public boolean isSame(Item otherItem) throws RepositoryException {
        return getDelegate().isSame(unwrap(otherItem));
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void save() throws RepositoryException {
        getActionHandler().beforeItemSave(this);
        getDelegate().save();
        getActionHandler().afterItemSave(this);
    }

    public void refresh(boolean keepChanges) throws RepositoryException {
        getActionHandler().beforeItemRefresh(this, keepChanges);
        getDelegate().refresh(keepChanges);
        getActionHandler().afterItemRefresh(this, keepChanges);
    }

    public void remove() throws RepositoryException {
        // handler is notified from subclasses
        getDelegate().remove();
    }
}
