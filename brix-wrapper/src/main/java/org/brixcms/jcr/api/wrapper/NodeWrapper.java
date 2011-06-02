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

package org.brixcms.jcr.api.wrapper;

import org.brixcms.jcr.api.JcrItem;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrProperty;
import org.brixcms.jcr.api.JcrPropertyIterator;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrSession.Behavior;
import org.brixcms.jcr.api.JcrVersion;
import org.brixcms.jcr.api.JcrVersionHistory;

import javax.jcr.Binary;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public class NodeWrapper extends ItemWrapper implements JcrNode {
    public static JcrNode wrap(Node delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            Behavior behavior = session.getBehavior();
            if (behavior != null) {
                JcrNode node = behavior.wrap(delegate, session);
                if (node != null) {
                    return node;
                }
            }
            return new NodeWrapper(delegate, session);
        }
    }

    public NodeWrapper(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    @Override
    public String toString() {
        return getPath() + " [" + getPrimaryNodeType().getName() + "]";
    }



    public void accept(final ItemVisitor visitor) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                visitor.visit(NodeWrapper.this);
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    @Override
    public void save() {
        Behavior behavior = getJcrSession().getBehavior();
        if (behavior != null) {
            behavior.nodeSaved(this);
        }
        super.getJcrSession().save();
    }

    @Override
    public Node getDelegate() {
        return (Node) super.getDelegate();
    }


    public JcrNode addNode(final String relPath) {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().addNode(relPath), getJcrSession());
            }
        });
    }

    public JcrNode addNode(final String relPath, final String primaryNodeTypeName) {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().addNode(relPath, primaryNodeTypeName),
                        getJcrSession());
            }
        });
    }

    public void orderBefore(final String srcChildRelPath, final String destChildRelPath) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().orderBefore(srcChildRelPath, destChildRelPath);
            }
        });
    }

    public JcrProperty setProperty(final String name, final Value value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrap(value)),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final Value value, final int type) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate()
                        .setProperty(name, unwrap(value), type), getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final Value[] values) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                final Value[] unwrapped = unwrap(values, new Value[values.length]);
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrapped),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final Value[] values, final int type) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                final Value[] unwrapped = unwrap(values, new Value[values.length]);
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrapped, type),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final String[] values) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, values),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final String[] values, final int type) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, values, type),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final String value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final String value, final int type) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value, type),
                        getJcrSession());
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public JcrProperty setProperty(final String name, final InputStream value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final Binary value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrap(value)),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final boolean value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final double value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final BigDecimal value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrap(value)),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final long value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final Calendar value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, value),
                        getJcrSession());
            }
        });
    }

    public JcrProperty setProperty(final String name, final Node value) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper.wrap(getDelegate().setProperty(name, unwrap(value)),
                        getJcrSession());
            }
        });
    }

    public JcrNode getNode(final String relPath) {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().getNode(relPath), getJcrSession());
            }
        });
    }

    public JcrNodeIterator getNodes() {
        return executeCallback(new Callback<JcrNodeIterator>() {
            public JcrNodeIterator execute() throws Exception {
                return JcrNodeIterator.Wrapper.wrap(getDelegate().getNodes(), getJcrSession());
            }
        });
    }

    public JcrNodeIterator getNodes(final String namePattern) {
        return executeCallback(new Callback<JcrNodeIterator>() {
            public JcrNodeIterator execute() throws Exception {
                return JcrNodeIterator.Wrapper.wrap(getDelegate().getNodes(namePattern),
                        getJcrSession());
            }
        });
    }

    public JcrNodeIterator getNodes(final String[] nameGlobs) {
        return executeCallback(new Callback<JcrNodeIterator>() {
            public JcrNodeIterator execute() throws Exception {
                return JcrNodeIterator.Wrapper.wrap(getDelegate().getNodes(nameGlobs),
                        getJcrSession());
            }
        });
    }

    public JcrProperty getProperty(final String relPath) {
        return executeCallback(new Callback<JcrProperty>() {
            public JcrProperty execute() throws Exception {
                return JcrProperty.Wrapper
                        .wrap(getDelegate().getProperty(relPath), getJcrSession());
            }
        });
    }

    public JcrPropertyIterator getProperties() {
        return executeCallback(new Callback<JcrPropertyIterator>() {
            public JcrPropertyIterator execute() throws Exception {
                return JcrPropertyIterator.Wrapper.wrap(getDelegate().getProperties(),
                        getJcrSession());
            }
        });
    }

    public JcrPropertyIterator getProperties(final String namePattern) {
        return executeCallback(new Callback<JcrPropertyIterator>() {
            public JcrPropertyIterator execute() throws Exception {
                return JcrPropertyIterator.Wrapper.wrap(getDelegate().getProperties(namePattern),
                        getJcrSession());
            }
        });
    }

    public JcrPropertyIterator getProperties(final String[] nameGlobs) {
        return executeCallback(new Callback<JcrPropertyIterator>() {
            public JcrPropertyIterator execute() throws Exception {
                return JcrPropertyIterator.Wrapper.wrap(getDelegate().getProperties(nameGlobs),
                        getJcrSession());
            }
        });
    }

    public JcrItem getPrimaryItem() {
        return executeCallback(new Callback<JcrItem>() {
            public JcrItem execute() throws Exception {
                return JcrItem.Wrapper.wrap(getDelegate().getPrimaryItem(), getJcrSession());
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public String getUUID() {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getUUID();
            }
        });
    }

    public String getIdentifier() {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getIdentifier();
            }
        });
    }

    public int getIndex() {
        return executeCallback(new Callback<Integer>() {
            public Integer execute() throws Exception {
                return getDelegate().getIndex();
            }
        });
    }

    public JcrPropertyIterator getReferences() {
        return executeCallback(new Callback<JcrPropertyIterator>() {
            public JcrPropertyIterator execute() throws Exception {
                return JcrPropertyIterator.Wrapper.wrap(getDelegate().getReferences(),
                        getJcrSession());
            }
        });
    }

    public JcrPropertyIterator getReferences(final String name) {
        return executeCallback(new Callback<JcrPropertyIterator>() {
            public JcrPropertyIterator execute() throws Exception {
                return JcrPropertyIterator.Wrapper.wrap(getDelegate().getReferences(name),
                        getJcrSession());
            }
        });
    }

    public JcrPropertyIterator getWeakReferences() {
        return executeCallback(new Callback<JcrPropertyIterator>() {
            public JcrPropertyIterator execute() throws Exception {
                return JcrPropertyIterator.Wrapper.wrap(getDelegate().getWeakReferences(), getJcrSession());
            }
        });
    }

    public JcrPropertyIterator getWeakReferences(final String name) {
        return executeCallback(new Callback<JcrPropertyIterator>() {
            public JcrPropertyIterator execute() throws Exception {
                return JcrPropertyIterator.Wrapper.wrap(getDelegate().getWeakReferences(name), getJcrSession());
            }
        });
    }

    public boolean hasNode(final String relPath) {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().hasNode(relPath);
            }
        });
    }

    public boolean hasProperty(final String relPath) {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().hasProperty(relPath);
            }
        });
    }

    public boolean hasNodes() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().hasNodes();
            }
        });
    }

    public boolean hasProperties() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().hasProperties();
            }
        });
    }

    public NodeType getPrimaryNodeType() {
        return executeCallback(new Callback<NodeType>() {
            public NodeType execute() throws Exception {
                return getDelegate().getPrimaryNodeType();
            }
        });
    }

    public NodeType[] getMixinNodeTypes() {
        return executeCallback(new Callback<NodeType[]>() {
            public NodeType[] execute() throws Exception {
                return getDelegate().getMixinNodeTypes();
            }
        });
    }

    public boolean isNodeType(final String nodeTypeName) {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().isNodeType(nodeTypeName);
            }
        });
    }

    public void setPrimaryType(final String nodeTypeName) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().setPrimaryType(nodeTypeName);
            }
        });
    }

    public void addMixin(final String mixinName) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().addMixin(mixinName);
            }
        });
    }

    public void removeMixin(final String mixinName) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().removeMixin(mixinName);
            }
        });
    }

    public boolean canAddMixin(final String mixinName) {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().canAddMixin(mixinName);
            }
        });
    }

    public NodeDefinition getDefinition() {
        return executeCallback(new Callback<NodeDefinition>() {
            public NodeDefinition execute() throws Exception {
                return getDelegate().getDefinition();
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public JcrVersion checkin() {
        return executeCallback(new Callback<JcrVersion>() {
            public JcrVersion execute() throws Exception {
                final Node delegate = getDelegate();

                if (delegate instanceof Version) {
                    VersionManager vm = delegate.getSession().getWorkspace().getVersionManager();
                    if (vm.isCheckedOut(delegate.getPath())) {
                        return JcrVersion.Wrapper.wrap(vm.checkin(delegate.getPath()), getJcrSession());
                    }
                }
                return null;
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void checkout() {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                if (getDelegate() instanceof Version) {
                    VersionManager vm = getDelegate().getSession().getWorkspace().getVersionManager();
                    vm.checkout(getDelegate().getPath());
                }
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void doneMerge(final Version version) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().doneMerge(unwrap(version));
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void cancelMerge(final Version version) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().cancelMerge(unwrap(version));
            }
        });
    }

    public void update(final String srcWorkspaceName) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().update(srcWorkspaceName);
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public JcrNodeIterator merge(final String srcWorkspace, final boolean bestEffort) {
        return executeCallback(new Callback<JcrNodeIterator>() {
            public JcrNodeIterator execute() throws Exception {
                return JcrNodeIterator.Wrapper.wrap(getDelegate().merge(srcWorkspace, bestEffort),
                        getJcrSession());
            }
        });
    }

    public String getCorrespondingNodePath(final String workspaceName) {
        return executeCallback(new Callback<String>() {
            public String execute() throws Exception {
                return getDelegate().getCorrespondingNodePath(workspaceName);
            }
        });
    }

    public JcrNodeIterator getSharedSet() {
        return executeCallback(new Callback<JcrNodeIterator>() {
            public JcrNodeIterator execute() throws Exception {
                return JcrNodeIterator.Wrapper.wrap(getDelegate().getSharedSet(), getJcrSession());
            }
        });
    }

    public void removeSharedSet() {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().removeSharedSet();
            }
        });
    }

    public void removeShare() {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().removeShare();
            }
        });
    }

    public boolean isCheckedOut() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().isCheckedOut();
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void restore(final String versionName, final boolean removeExisting) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().restore(versionName, removeExisting);
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void restore(final Version version, final boolean removeExisting) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().restore(unwrap(version), removeExisting);
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void restore(final Version version, final String relPath, final boolean removeExisting) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().restore(unwrap(version), relPath, removeExisting);
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void restoreByLabel(final String versionLabel, final boolean removeExisting) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().restoreByLabel(versionLabel, removeExisting);
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public JcrVersionHistory getVersionHistory() {
        return executeCallback(new Callback<JcrVersionHistory>() {
            public JcrVersionHistory execute() throws Exception {
                return JcrVersionHistory.Wrapper.wrap(getDelegate().getVersionHistory(),
                        getJcrSession());
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public JcrVersion getBaseVersion() {
        return executeCallback(new Callback<JcrVersion>() {
            public JcrVersion execute() throws Exception {
                return JcrVersion.Wrapper.wrap(getDelegate().getBaseVersion(), getJcrSession());
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public Lock lock(final boolean isDeep, final boolean isSessionScoped) {
        return executeCallback(new Callback<Lock>() {
            public Lock execute() throws Exception {
                return getDelegate().lock(isDeep, isSessionScoped);
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public Lock getLock() {
        return executeCallback(new Callback<Lock>() {
            public Lock execute() throws Exception {
                return getDelegate().getSession().getWorkspace().getLockManager().getLock(getDelegate().getPath());
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public void unlock() {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().unlock();
            }
        });
    }

    /**
     * @depreated
     */
    @Deprecated
    public boolean holdsLock() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().holdsLock();
            }
        });
    }

    public boolean isLocked() {
        return executeCallback(new Callback<Boolean>() {
            public Boolean execute() throws Exception {
                return getDelegate().isLocked();
            }
        });
    }

    public void followLifecycleTransition(final String transition) {
        executeCallback(new VoidCallback() {
            public void execute() throws Exception {
                getDelegate().followLifecycleTransition(transition);
            }
        });
    }

    public String[] getAllowedLifecycleTransistions() {
        return executeCallback(new Callback<String[]>() {
            public String[] execute() throws Exception {
                return getDelegate().getAllowedLifecycleTransistions();
            }
        });
    }
}
