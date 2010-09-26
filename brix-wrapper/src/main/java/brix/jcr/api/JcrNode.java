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

package brix.jcr.api;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.jcr.Binary;
import javax.jcr.InvalidLifecycleTransitionException;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrNode extends JcrItem, Node
{

    public static class Wrapper
    {
        public static JcrNode wrap(Node delegate, JcrSession session)
        {
            return WrapperAccessor.JcrNodeWrapper.wrap(delegate, session);
        }
    };

    public Node getDelegate();

    public void addMixin(String mixinName);

    public JcrNode addNode(String relPath);

    public JcrNode addNode(String relPath, String primaryNodeTypeName);

    public boolean canAddMixin(String mixinName);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#cancelMerge} should be
     *             used instead.
     */
    @Deprecated
    public void cancelMerge(Version version);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#checkin} should be used
     *             instead.
     */
    @Deprecated
    public JcrVersion checkin();

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#checkout} should be used
     *             instead.
     */
    @Deprecated
    public void checkout();

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#doneMerge} should be used
     *             instead.
     */
    @Deprecated
    public void doneMerge(Version version);


    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#getBaseVersion} should be
     *             used instead.
     */
    @Deprecated
    public JcrVersion getBaseVersion();

    public String getCorrespondingNodePath(String workspaceName);

    public NodeDefinition getDefinition();

    public int getIndex();

    /**
     * @deprecated As of JCR 2.0, {@link LockManager#getLock(String)} should be used instead.
     */
    @Deprecated
    public Lock getLock();

    public NodeType[] getMixinNodeTypes();

    public JcrNode getNode(String relPath);

    public JcrNodeIterator getNodes();

    public JcrNodeIterator getNodes(String namePattern);

    public JcrItem getPrimaryItem();

    public NodeType getPrimaryNodeType();

    public JcrPropertyIterator getProperties();

    public JcrPropertyIterator getProperties(String namePattern);

    public JcrProperty getProperty(String relPath);

    public JcrPropertyIterator getReferences();

    /**
     * @deprecated As of JCR 2.0, {@link #getIdentifier()} should be used instead.
     */
    @Deprecated
    public String getUUID();

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#getVersionHistory} should
     *             be used instead.
     */
    @Deprecated
    public JcrVersionHistory getVersionHistory();

    public boolean hasNode(String relPath);

    public boolean hasNodes();

    public boolean hasProperties();

    public boolean hasProperty(String relPath);

    /**
     * @deprecated As of JCR 2.0, {@link LockManager#holdsLock(String)} should be used instead.
     */
    @Deprecated
    public boolean holdsLock();

    public boolean isCheckedOut();

    public boolean isLocked();

    public boolean isNodeType(String nodeTypeName);

    /**
     * @deprecated As of JCR 2.0, {@link LockManager#lock(String, boolean, boolean, long, String)}
     *             should be used instead.
     */
    @Deprecated
    public Lock lock(boolean isDeep, boolean isSessionScoped);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#merge} should be used
     *             instead.
     */
    @Deprecated
    public JcrNodeIterator merge(String srcWorkspace, boolean bestEffort);

    public void orderBefore(String srcChildRelPath, String destChildRelPath);

    public void removeMixin(String mixinName);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#restore} should be used
     *             instead.
     */
    @Deprecated
    public void restore(String versionName, boolean removeExisting);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#restore} should be used
     *             instead.
     */
    @Deprecated
    public void restore(Version version, boolean removeExisting);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#restore} should be used
     *             instead.
     */
    @Deprecated
    public void restore(Version version, String relPath, boolean removeExisting);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#restoreByLabel} should be
     *             used instead.
     */
    @Deprecated
    public void restoreByLabel(String versionLabel, boolean removeExisting);

    public JcrProperty setProperty(String name, Value value);

    public JcrProperty setProperty(String name, Value[] values);

    public JcrProperty setProperty(String name, String[] values);

    public JcrProperty setProperty(String name, String value);

    /**
     * @deprecated As of JCR 2.0, {@link #setProperty(String, Binary)} should be used instead.
     */
    @Deprecated
    public JcrProperty setProperty(String name, InputStream value);

    public JcrProperty setProperty(String name, boolean value);

    public JcrProperty setProperty(String name, double value);

    public JcrProperty setProperty(String name, long value);

    public JcrProperty setProperty(String name, Calendar value);

    public JcrProperty setProperty(String name, Node value);

    public JcrProperty setProperty(String name, Value value, int type);

    public JcrProperty setProperty(String name, Value[] values, int type);

    public JcrProperty setProperty(String name, String[] values, int type);

    public JcrProperty setProperty(String name, String value, int type);

    /**
     * @deprecated As of JCR 2.0, {@link LockManager#unlock(String)} should be used instead.
     */
    @Deprecated
    public void unlock();

    public void update(String srcWorkspaceName);

    /**
     * The behavior of this method is identical to that of
     * {@link #setProperty(String name, Value value)} except that the value is specified as a
     * {@link Binary} and, if possible, the type assigned to the property is <code>BINARY</code>,
     * otherwise a best-effort conversion is attempted.
     * 
     * @param name
     *            The name of a property of this node
     * @param value
     *            The value to assigned
     * @return The updated <code>Property</code> object
     * @throws ValueFormatException
     *             if <code>value</code> cannot be converted to the type of the specified property
     *             or if the property already exists and is multi-valued.
     * @throws VersionException
     *             if this node is read-only due to a checked-in node and this implementation
     *             performs this validation immediately.
     * @throws LockException
     *             if a lock prevents the setting of the property and this implementation performs
     *             this validation immediately.
     * @throws ConstraintViolationException
     *             if the change would violate a node-type or other constraint and this
     *             implementation performs this validation immediately.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public JcrProperty setProperty(String name, Binary value);

    /**
     * The behavior of this method is identical to that of
     * {@link #setProperty(String name, Value value)} except that the value is specified as a
     * {@link BigDecimal} and, if possible, the type assigned to the property is
     * <code>DECIMAL</code>, otherwise a best-effort conversion is attempted.
     * 
     * @param name
     *            The name of a property of this node
     * @param value
     *            The value to assigned
     * @return The updated <code>Property</code> object
     * @throws ValueFormatException
     *             if <code>value</code> cannot be converted to the type of the specified property
     *             or if the property already exists and is multi-valued.
     * @throws VersionException
     *             if this node is read-only due to a checked-in node and this implementation
     *             performs this validation immediately.
     * @throws LockException
     *             if a lock prevents the setting of the property and this implementation performs
     *             this validation immediately.
     * @throws ConstraintViolationException
     *             if the change would violate a node-type or other constraint and this
     *             implementation performs this validation immediately.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public JcrProperty setProperty(String name, BigDecimal value);

    /**
     * Gets all child nodes of this node accessible through the current <code>Session</code> that
     * match one or more of the <code>nameGlob</code> strings in the passed array.
     * <p>
     * A glob may be a full name or a partial name with one or more wildcard characters ("
     * <code>*</code>"). For example,
     * <p>
     * <code>N.getNodes(new String[] {"jcr:*", "myapp:report", "my
     * doc"})</code>
     * <p>
     * would return a <code>NodeIterator</code> holding all accessible child nodes of <code>N</code>
     * that are either called '<code>myapp:report</code>', begin with the prefix '<code>jcr:</code>'
     * or are called '<code>my
     * doc</code>'.
     * <p>
     * Note that unlike in the case of the {@link #getNodes(String)} leading and trailing whitespace
     * around a glob is <i>not</i> ignored.
     * <p>
     * The globs are matched against the names (not the paths) of the immediate child nodes of this
     * node.
     * <p>
     * If this node has no accessible matching child nodes, then an empty iterator is returned.
     * <p>
     * The same reacquisition semantics apply as with <code>{@link
     * #getNode(String)}</code>.
     * 
     * @param nameGlobs
     *            an array of globbing strings.
     * @return a <code>NodeIterator</code>.
     * @throws RepositoryException
     *             if an unexpected error occurs.
     * @since JCR 2.0
     */
    public JcrNodeIterator getNodes(String[] nameGlobs);

    /**
     * Gets all properties of this node accessible through the current <code>Session</code> that
     * match one or more of the <code>nameGlob</code> strings in the passed array.
     * <p>
     * A glob may be a full name or a partial name with one or more wildcard characters ("
     * <code>*</code>"). For example,
     * <p>
     * <code>N.getProperties(new String[] {"jcr:*", "myapp:report", "my
     * doc"})</code>
     * <p>
     * would return a <code>PropertyIterator</code> holding all accessible properties of
     * <code>N</code> that are either called '<code>myapp:report</code>', begin with the prefix '
     * <code>jcr:</code>' or are called '<code>my doc</code>'.
     * <p>
     * Note that unlike in the case of the {@link #getProperties(String)} leading and trailing
     * whitespace around a glob is <i>not</i> ignored.
     * <p>
     * The globs are matched against the names (not the paths) of the properties of this node.
     * <p>
     * If this node has no accessible matching properties, then an empty iterator is returned.
     * <p>
     * The same reacquisition semantics apply as with <code>{@link
     * #getProperty(String)}</code>.
     * 
     * @param nameGlobs
     *            an array of globbing strings.
     * @return a <code>PropertyIterator</code>.
     * @throws RepositoryException
     *             if an unexpected error occurs.
     * @since JCR 2.0
     */
    public JcrPropertyIterator getProperties(String[] nameGlobs);

    /**
     * Returns the identifier of this node. Applies to both referenceable and non-referenceable
     * nodes.
     * <p>
     * A <code>RepositoryException</code> is thrown if an error occurs.
     * 
     * @return the identifier of this node.
     * @throws RepositoryException
     *             if an error occurs.
     * @since JCR 2.0
     */
    public String getIdentifier();

    /**
     * This method returns all <code>REFERENCE</code> properties that refer to this node, have the
     * specified <code>name</code> and that are accessible through the current <code>Session</code>.
     * <p>
     * If the <code>name</code> parameter is <code>null</code> then all referring
     * <code>REFERENCES</code> are returned regardless of name.
     * <p>
     * Some implementations may only return properties that have been persisted. Some may return
     * both properties that have been persisted and those that have been dispatched but not
     * persisted (for example, those saved within a transaction but not yet committed) while others
     * implementations may return these two categories of property as well as properties that are
     * still pending and not yet dispatched.
     * <p>
     * In implementations that support versioning, this method does not return properties that are
     * part of the frozen state of a version in version storage.
     * <p>
     * If this node has no referring <code>REFERENCE</code> properties with the specified name, an
     * empty iterator is returned. This includes the case where this node is not referenceable.
     * 
     * @param name
     *            name of referring <code>REFERENCE</code> properties to be returned; if
     *            <code>null</code> then all referring <code>REFERENCE</code>s are returned.
     * @return A <code>PropertyIterator</code>.
     * @throws RepositoryException
     *             if an error occurs.
     * @since JCR 2.0
     */
    public JcrPropertyIterator getReferences(String name);

    /**
     * This method returns all <code>WEAKREFERENCE</code> properties that refer to this node and
     * that are accessible through the current <code>Session</code>. Equivalent to
     * <code>Node.getWeakReferences(null)</code>.
     * <p>
     * If this node has no referring <code>WEAKREFERENCE</code> properties, an empty iterator is
     * returned. This includes the case where this node is not referenceable.
     * 
     * @return A <code>PropertyIterator</code>.
     * @throws RepositoryException
     *             if an error occurs.
     * @see #getWeakReferences(String).
     * @since JCR 2.0
     */
    public JcrPropertyIterator getWeakReferences();

    /**
     * This method returns all <code>WEAKREFERENCE</code> properties that refer to this node, have
     * the specified <code>name</code> and that are accessible through the current
     * <code>Session</code>.
     * <p>
     * If the <code>name</code> parameter is <code>null</code> then all referring
     * <code>WEAKREFERENCE</code> are returned regardless of name.
     * <p>
     * Some implementations may only return properties that have been persisted. Some may return
     * both properties that have been persisted and those that have been dispatched but not
     * persisted (for example, those saved within a transaction but not yet committed) while others
     * implementations may return these two categories of property as well as properties that are
     * still pending and not yet dispatched.
     * <p>
     * In implementations that support versioning, this method does not return properties that are
     * part of the frozen state of a version in version storage.
     * <p>
     * If this node has no referring <code>WEAKREFERENCE</code> properties with the specified name,
     * an empty iterator is returned. This includes the case where this node is not referenceable.
     * 
     * @param name
     *            name of referring <code>WEAKREFERENCE</code> properties to be returned; if
     *            <code>null</code> then all referring <code>WEAKREFERENCE</code>s are returned.
     * @return A <code>PropertyIterator</code>.
     * @throws RepositoryException
     *             if an error occurs.
     * @since JCR 2.0
     */
    public JcrPropertyIterator getWeakReferences(String name);

    /**
     * Changes the primary node type of this node to <code>nodeTypeName</code>. Also immediately
     * changes this node's <code>jcr:primaryType</code> property appropriately. Semantically, the
     * new node type may take effect immediately or on dispatch but <i>must</i> take effect on
     * persist. The behavior adopted must be the same as the behavior adopted for {@link #addMixin}
     * and the behavior that occurs when a node is first created.
     * <p>
     * If the presence of an existing property or child node would cause an incompatibility with the
     * new node type then a <code>ConstraintViolationException</code> is thrown either immediately,
     * on dispatch or on persist.
     * <p>
     * If the new node type would cause this node to be incompatible with the node type of its
     * parent then a <code>ConstraintViolationException</code> is thrown either immediately, on
     * dispatch or on persist.
     * <p>
     * A <code>ConstraintViolationException</code> is also thrown either immediately, on dispatch or
     * on persist if a conflict with an already assigned mixin occurs.
     * <p>
     * A <code>ConstraintViolationException</code> may also be thrown either immediately , on
     * dispatch or on persist if the attempted change violates implementation-specific node type
     * transition rules. A repository that disallows all primary node type changes would simple
     * throw this exception in all cases.
     * <p>
     * If the specified node type is not recognized a <code>NoSuchNodeTypeException</code> is thrown
     * either immediately, on dispatch or on persist.
     * <p>
     * A <code>VersionException</code> is thrown either immediately , on dispatch or on persist if
     * this node is read-only dues to a check-in.
     * <p>
     * A <code>LockException</code> is thrown either immediately, on dispatch or on persist if a
     * lock prevents the change of node type.
     * 
     * @param nodeTypeName
     *            the name of the new node type.
     * @throws ConstraintViolationException
     *             if the specified primary node type creates a type conflict and this
     *             implementation performs this validation immediately.
     * @throws NoSuchNodeTypeException
     *             If the specified <code>nodeTypeName</code> is not recognized and this
     *             implementation performs this validation immediately.
     * @throws VersionException
     *             if this node is read-only due to a checked-in node and this implementation
     *             performs this validation immediately.
     * @throws LockException
     *             if a lock prevents the change of the primary node type and this implementation
     *             performs this validation immediately.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public void setPrimaryType(String nodeTypeName);


    /**
     * Returns an iterator over all nodes that are in the shared set of this node. If this node is
     * not shared then the returned iterator contains only this node.
     * 
     * @return a <code>NodeIterator</code>.
     * @throws RepositoryException
     *             if an error occurs.
     * @since JCR 2.0
     */
    public JcrNodeIterator getSharedSet();

    /**
     * Removes this node and every other node in the shared set of this node.
     * <p>
     * This removal must be done atomically, i.e., if one of the nodes cannot be removed, the method
     * throws the exception {@link Node#remove()} would have thrown in that case, and none of the
     * nodes are removed.
     * <p>
     * If this node is not shared this method removes only this node.
     * 
     * @throws VersionException
     *             if the parent node of this item is versionable and checked-in or is
     *             non-versionable but its nearest versionable ancestor is checked-in and this
     *             implementation performs this validation immediately instead of waiting until
     *             <code>save</code>.
     * @throws LockException
     *             if a lock prevents the removal of this item and this implementation performs this
     *             validation immediately instead of waiting until <code>save</code>.
     * @throws ConstraintViolationException
     *             if removing the specified item would violate a node type or
     *             implementation-specific constraint and this implementation performs this
     *             validation immediately instead of waiting until <code>save</code>.
     * @throws RepositoryException
     *             if another error occurs.
     * @see #removeShare()
     * @see Item#remove()
     * @see javax.jcr.Session#removeItem(String)
     * @since JCR 2.0
     */
    public void removeSharedSet();

    /**
     * Removes this node, but does not remove any other node in the shared set of this node.
     * 
     * @throws VersionException
     *             if the parent node of this item is versionable and checked-in or is
     *             non-versionable but its nearest versionable ancestor is checked-in and this
     *             implementation performs this validation immediately instead of waiting until
     *             <code>save</code>.
     * @throws LockException
     *             if a lock prevents the removal of this item and this implementation performs this
     *             validation immediately instead of waiting until <code>save</code>.
     * @throws ConstraintViolationException
     *             if removing the specified item would violate a node type or
     *             implementation-specific constraint and this implementation performs this
     *             validation immediately instead of waiting until <code>save</code>.
     * @throws RepositoryException
     *             if if this node cannot be removed without removing another node in the shared set
     *             of this node or another error occurs.
     * @see #removeSharedSet()
     * @see Item#remove()
     * @see javax.jcr.Session#removeItem(String)
     * @since JCR 2.0
     */
    public void removeShare();

    /**
     * Causes the lifecycle state of this node to undergo the specified <code>transition</code>.
     * <p>
     * This method may change the value of the <code>jcr:currentLifecycleState</code> property, in
     * most cases it is expected that the implementation will change the value to that of the passed
     * <code>transition</code> parameter, though this is an implementation-specific issue. If the
     * <code>jcr:currentLifecycleState</code> property is changed the change is persisted
     * immediately, there is no need to call <code>save</code>.
     * 
     * @param transition
     *            a state transition
     * @throws UnsupportedRepositoryOperationException
     *             if this implementation does not support lifecycle actions or if this node does
     *             not have the <code>mix:lifecycle</code> mixin.
     * @throws InvalidLifecycleTransitionException
     *             if the lifecycle transition is not successful.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public void followLifecycleTransition(String transition);

    /**
     * Returns the list of valid state transitions for this node.
     * 
     * @return a <code>String</code> array.
     * @throws UnsupportedRepositoryOperationException
     *             if this implementation does not support lifecycle actions or if this node does
     *             not have the <code>mix:lifecycle</code> mixin.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public String[] getAllowedLifecycleTransistions();


}