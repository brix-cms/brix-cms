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

package brix.jcr.helper;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.Item;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;

import brix.jcr.api.JcrItem;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrPropertyIterator;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrVersion;
import brix.jcr.api.JcrVersionHistory;

public class JcrNodeDecorator implements JcrNode
{
    private final JcrNode delegate;

    public JcrNodeDecorator(JcrNode delegate)
    {
        if (delegate == null)
        {
            throw new IllegalArgumentException("delegate cannot be null");
        }
        this.delegate = delegate;
    }

    public void accept(ItemVisitor visitor)
    {
        delegate.accept(visitor);
    }

    public void addMixin(String mixinName)
    {
        delegate.addMixin(mixinName);
    }

    public JcrNode addNode(String relPath, String primaryNodeTypeName)
    {
        return delegate.addNode(relPath, primaryNodeTypeName);
    }

    public JcrNode addNode(String relPath)
    {
        return delegate.addNode(relPath);
    }

    public boolean canAddMixin(String mixinName)
    {
        return delegate.canAddMixin(mixinName);
    }

    /** @deprecated */
    @Deprecated
    public void cancelMerge(Version version)
    {
        delegate.cancelMerge(version);
    }

    /** @deprecated */
    @Deprecated
    public JcrVersion checkin()
    {
        return delegate.checkin();
    }

    /** @deprecated */
    @Deprecated
    public void checkout()
    {
        delegate.checkout();
    }

    /** @deprecated */
    @Deprecated
    public void doneMerge(Version version)
    {
        delegate.doneMerge(version);
    }

    public JcrItem getAncestor(int depth)
    {
        return delegate.getAncestor(depth);
    }

    /** @deprecated */
    @Deprecated
    public JcrVersion getBaseVersion()
    {
        return delegate.getBaseVersion();
    }

    public String getCorrespondingNodePath(String workspaceName)
    {
        return delegate.getCorrespondingNodePath(workspaceName);
    }

    public NodeDefinition getDefinition()
    {
        return delegate.getDefinition();
    }

    public Node getDelegate()
    {
        return delegate.getDelegate();
    }

    public int getDepth()
    {
        return delegate.getDepth();
    }

    public int getIndex()
    {
        return delegate.getIndex();
    }

    /** @deprecated */
    @Deprecated
    public Lock getLock()
    {
        return delegate.getLock();
    }

    public NodeType[] getMixinNodeTypes()
    {
        return delegate.getMixinNodeTypes();
    }

    public String getName()
    {
        return delegate.getName();
    }

    public JcrNode getNode(String relPath)
    {
        return delegate.getNode(relPath);
    }

    public JcrNodeIterator getNodes()
    {
        return delegate.getNodes();
    }

    public JcrNodeIterator getNodes(String namePattern)
    {
        return delegate.getNodes(namePattern);
    }

    public JcrNode getParent()
    {
        return delegate.getParent();
    }

    public String getPath()
    {
        return delegate.getPath();
    }

    public JcrItem getPrimaryItem()
    {
        return delegate.getPrimaryItem();
    }

    public NodeType getPrimaryNodeType()
    {
        return delegate.getPrimaryNodeType();
    }

    public JcrPropertyIterator getProperties()
    {
        return delegate.getProperties();
    }

    public JcrPropertyIterator getProperties(String namePattern)
    {
        return delegate.getProperties(namePattern);
    }

    public JcrProperty getProperty(String relPath)
    {
        return delegate.getProperty(relPath);
    }

    public JcrPropertyIterator getReferences()
    {
        return delegate.getReferences();
    }

    public JcrSession getSession()
    {
        return delegate.getSession();
    }

    /** @deprecated */
    @Deprecated
    public String getUUID()
    {
        return delegate.getUUID();
    }

    /** @deprecated */
    @Deprecated
    public JcrVersionHistory getVersionHistory()
    {
        return delegate.getVersionHistory();
    }

    public boolean hasNode(String relPath)
    {
        return delegate.hasNode(relPath);
    }

    public boolean hasNodes()
    {
        return delegate.hasNodes();
    }

    public boolean hasProperties()
    {
        return delegate.hasProperties();
    }

    public boolean hasProperty(String relPath)
    {
        return delegate.hasProperty(relPath);
    }

    /** @deprecated */
    @Deprecated
    public boolean holdsLock()
    {
        return delegate.holdsLock();
    }

    public boolean isCheckedOut()
    {
        return delegate.isCheckedOut();
    }

    public boolean isLocked()
    {
        return delegate.isLocked();
    }

    public boolean isModified()
    {
        return delegate.isModified();
    }

    public boolean isNew()
    {
        return delegate.isNew();
    }

    public boolean isNode()
    {
        return delegate.isNode();
    }

    public boolean isNodeType(String nodeTypeName)
    {
        return delegate.isNodeType(nodeTypeName);
    }

    public boolean isSame(Item otherItem)
    {
        return delegate.isSame(otherItem);
    }

    /** @deprecated */
    @Deprecated
    public Lock lock(boolean isDeep, boolean isSessionScoped)
    {
        return delegate.lock(isDeep, isSessionScoped);
    }

    /** @deprecated */
    @Deprecated
    public JcrNodeIterator merge(String srcWorkspace, boolean bestEffort)
    {
        return delegate.merge(srcWorkspace, bestEffort);
    }

    public void orderBefore(String srcChildRelPath, String destChildRelPath)
    {
        delegate.orderBefore(srcChildRelPath, destChildRelPath);
    }

    public void refresh(boolean keepChanges)
    {
        delegate.refresh(keepChanges);
    }

    public void remove()
    {
        delegate.remove();
    }

    public void removeMixin(String mixinName)
    {
        delegate.removeMixin(mixinName);
    }

    /** @deprecated */
    @Deprecated
    public void restore(String versionName, boolean removeExisting)
    {
        delegate.restore(versionName, removeExisting);
    }

    /** @deprecated */
    @Deprecated
    public void restore(Version version, boolean removeExisting)
    {
        delegate.restore(version, removeExisting);
    }

    /** @deprecated */
    @Deprecated
    public void restore(Version version, String relPath, boolean removeExisting)
    {
        delegate.restore(version, relPath, removeExisting);
    }

    /** @deprecated */
    @Deprecated
    public void restoreByLabel(String versionLabel, boolean removeExisting)
    {
        delegate.restoreByLabel(versionLabel, removeExisting);
    }

    /** @deprecated */
    @Deprecated
    public void save()
    {
        delegate.save();
    }

    public JcrProperty setProperty(String name, boolean value)
    {
        return delegate.setProperty(name, value);
    }

    public JcrProperty setProperty(String name, Calendar value)
    {
        return delegate.setProperty(name, value);
    }

    public JcrProperty setProperty(String name, double value)
    {
        return delegate.setProperty(name, value);
    }

    /** @deprecated */
    @Deprecated
    public JcrProperty setProperty(String name, InputStream value)
    {
        return delegate.setProperty(name, value);
    }

    public JcrProperty setProperty(String name, long value)
    {
        return delegate.setProperty(name, value);
    }

    public JcrProperty setProperty(String name, Node value)
    {
        return delegate.setProperty(name, value);
    }

    public JcrProperty setProperty(String name, String value, int type)
    {
        return delegate.setProperty(name, value, type);
    }

    public JcrProperty setProperty(String name, String value)
    {
        return delegate.setProperty(name, value);
    }

    public JcrProperty setProperty(String name, String[] values, int type)
    {
        return delegate.setProperty(name, values, type);
    }

    public JcrProperty setProperty(String name, String[] values)
    {
        return delegate.setProperty(name, values);
    }

    public JcrProperty setProperty(String name, Value value, int type)
    {
        return delegate.setProperty(name, value, type);
    }

    public JcrProperty setProperty(String name, Value value)
    {
        return delegate.setProperty(name, value);
    }

    public JcrProperty setProperty(String name, Value[] values, int type)
    {
        return delegate.setProperty(name, values, type);
    }

    public JcrProperty setProperty(String name, Value[] values)
    {
        return delegate.setProperty(name, values);
    }

    /** @deprecated */
    @Deprecated
    public void unlock()
    {
        delegate.unlock();
    }

    public void update(String srcWorkspaceName)
    {
        delegate.update(srcWorkspaceName);
    }

    public void followLifecycleTransition(String transition)
    {
        delegate.followLifecycleTransition(transition);

    }

    public String[] getAllowedLifecycleTransistions()
    {
        return delegate.getAllowedLifecycleTransistions();
    }

    public String getIdentifier()
    {
        return delegate.getIdentifier();
    }

    public NodeIterator getNodes(String[] nameGlobs)
    {
        return delegate.getNodes(nameGlobs);
    }

    public PropertyIterator getProperties(String[] nameGlobs)
    {
        return delegate.getProperties(nameGlobs);
    }

    public PropertyIterator getReferences(String name)
    {
        return delegate.getReferences(name);
    }

    public NodeIterator getSharedSet()
    {
        return delegate.getSharedSet();
    }

    public PropertyIterator getWeakReferences()
    {
        return delegate.getWeakReferences();
    }

    public PropertyIterator getWeakReferences(String name)
    {
        return delegate.getWeakReferences(name);
    }

    public void removeShare()
    {
        delegate.removeShare();
    }

    public void removeSharedSet()
    {
        delegate.removeSharedSet();
    }

    public void setPrimaryType(String nodeTypeName)
    {
        delegate.setPrimaryType(nodeTypeName);
    }

    public Property setProperty(String name, Binary value)
    {
        return delegate.setProperty(name, value);
    }

    public Property setProperty(String name, BigDecimal value)
    {
        return delegate.setProperty(name, value);
    }


}
