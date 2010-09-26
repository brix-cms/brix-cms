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
import java.io.OutputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.Credentials;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;

import org.xml.sax.ContentHandler;

import brix.jcr.api.wrapper.WrapperAccessor;
import brix.jcr.base.BrixSession;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrSession extends BrixSession
{

    public static class Wrapper
    {
        public static JcrSession wrap(Session delegate, Behavior behavior)
        {
            return WrapperAccessor.JcrSessionWrapper.wrap(delegate, behavior);
        }

        public static JcrSession wrap(Session delegate)
        {
            return wrap(delegate, null);
        }
    };

    /**
     * Allows to customize behavior for nodes within the session to which it was passed.
     * 
     * @author Matej Knopp
     * 
     */
    public static interface Behavior
    {
        /**
         * Allows to use custom wrapper for given node.
         * 
         * @param node
         * @return
         */
        public JcrNode wrap(Node node, JcrSession session);

        /**
         * Invoked when the {@link JcrNode#save()} method was called on the given node.
         * 
         * @param node
         */
        public void nodeSaved(JcrNode node);

        /**
         * Invoked when the underlying JCR method throws an exception.
         * 
         * @param e
         */
        public void handleException(Exception e);
    };

    public Behavior getBehavior();

    public Session getDelegate();

    /**
     * @deprecated As of JCR 2.0, {@link LockManager#addLockToken(String)} should be used instead.
     */
    @Deprecated
    public void addLockToken(final String lt);

    public void checkPermission(final String absPath, final String actions);

    public void exportDocumentView(final String absPath, final ContentHandler contentHandler,
            final boolean skipBinary, final boolean noRecurse);

    public void exportDocumentView(final String absPath, final OutputStream out,
            final boolean skipBinary, final boolean noRecurse);

    public void exportSystemView(final String absPath, final ContentHandler contentHandler,
            final boolean skipBinary, final boolean noRecurse);

    public void exportSystemView(final String absPath, final OutputStream out,
            final boolean skipBinary, final boolean noRecurse);

    public Object getAttribute(final String name);

    public String[] getAttributeNames();

    public ContentHandler getImportContentHandler(final String parentAbsPath, final int uuidBehavior);

    public JcrItem getItem(final String absPath);

    /**
     * @deprecated As of JCR 2.0, {@link LockManager#getLockTokens()} should be used instead.
     */
    @Deprecated
    public String[] getLockTokens();

    public String getNamespacePrefix(final String uri);

    public String[] getNamespacePrefixes();

    public String getNamespaceURI(final String prefix);

    /**
     * @deprecated As of JCR 2.0, {@link #getNodeByIdentifier(String)} should be used instead.
     */
    @Deprecated
    public JcrNode getNodeByUUID(final String uuid);

    public Repository getRepository();

    public JcrNode getRootNode();

    public String getUserID();

    public JcrValueFactory getValueFactory();

    public JcrWorkspace getWorkspace();

    public boolean hasPendingChanges();

    public JcrSession impersonate(final Credentials credentials);

    public void importXML(final String parentAbsPath, final InputStream in, final int uuidBehavior);

    public boolean isLive();

    public boolean itemExists(final String absPath);

    public void logout();

    public void move(final String srcAbsPath, final String destAbsPath);

    public void refresh(final boolean keepChanges);

    /**
     * @deprecated As of JCR 2.0, {@link LockManager#removeLockToken(String)} should be used
     *             instead.
     */
    @Deprecated
    public void removeLockToken(final String lt);

    public void save();

    public void setNamespacePrefix(final String prefix, final String uri);

    // Caching related methods

    /**
     * Each wrapped node should call this method when the remove() method is invoked on it.
     */
    public void nodeRemoved(JcrNode node);

    /**
     * Returns the node specified by the given identifier. Applies to both referenceable and
     * non-referenceable nodes.
     * 
     * @param id
     *            An identifier.
     * @return A <code>Node</code>.
     * @throws ItemNotFoundException
     *             if no node with the specified identifier exists or if this
     *             <code>Session<code> does not have read access to the
     *                               node with the specified identifier.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public JcrNode getNodeByIdentifier(String id);

    /**
     * Returns the node at the specified absolute path in the workspace.
     * 
     * @param absPath
     *            An absolute path.
     * @return the specified <code>Node</code>.
     * @throws PathNotFoundException
     *             If no accessible node is found at the specifed path.
     * @throws RepositoryException
     *             If another error occurs.
     * @since JCR 2.0
     */
    public JcrNode getNode(String absPath);

    /**
     * Returns the property at the specified absolute path in the workspace.
     * 
     * @param absPath
     *            An absolute path.
     * @return the specified <code>Property</code>.
     * @throws PathNotFoundException
     *             If no accessible property is found at the specified path.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public JcrProperty getProperty(String absPath);

    /**
     * Returns <code>true</code> if a node exists at <code>absPath</code> and this
     * <code>Session</code> has read access to it; otherwise returns <code>false</code>.
     * 
     * @param absPath
     *            An absolute path.
     * @return a <code>boolean</code>
     * @throws RepositoryException
     *             if <code>absPath</code> is not a well-formed absolute path.
     * @since JCR 2.0
     */
    public boolean nodeExists(String absPath);

    /**
     * Returns <code>true</code> if a property exists at <code>absPath</code> and this
     * <code>Session</code> has read access to it; otherwise returns <code>false</code>.
     * 
     * @param absPath
     *            An absolute path.
     * @return a <code>boolean</code>
     * @throws RepositoryException
     *             if <code>absPath</code> is not a well-formed absolute path.
     * @since JCR 2.0
     */
    boolean propertyExists(String absPath);

    /**
     * Removes the specified item and its subgraph.
     * <p>
     * This is a session-write method and therefore requires a <code>save</code> in order to
     * dispatch the change.
     * <p>
     * If a node with same-name siblings is removed, this decrements by one the indices of all the
     * siblings with indices greater than that of the removed node. In other words, a removal
     * compacts the array of same-name siblings and causes the minimal re-numbering required to
     * maintain the original order but leave no gaps in the numbering.
     * <p>
     * A <code>ReferentialIntegrityException</code> will be thrown on dispatch if the specified item
     * or an item in its subgraph is currently the target of a <code>REFERENCE</code> property
     * located in this workspace but outside the specified item's subgraph and the current
     * <code>Session</code> has read access to that <code>REFERENCE</code> property.
     * <p>
     * A <code>ConstraintViolationException</code> will be thrown either immediately, on dispatch or
     * on persist, if removing the specified item would violate a node type or
     * implementation-specific constraint. Implementations may differ on when this validation is
     * performed.
     * <p>
     * A <code>VersionException</code> will be thrown either immediately, on dispatch or on persist,
     * if the parent node of the specified item is read-only due to a checked-in node.
     * Implementations may differ on when this validation is performed.
     * <p>
     * A <code>LockException</code> will be thrown either immediately, on dispatch or on persist, if
     * a lock prevents the removal of the specified item. Implementations may differ on when this
     * validation is performed.
     * <p>
     * A <code>PathNotFoundException</code> will be thrown either immediately, on dispatch or on
     * persist, if no accessible item is found at at <code>absPath</code>.
     * <p>
     * A <code>AccessDeniedException</code> will be thrown either immediately, on dispatch or on
     * persist, if the specified item or an item in its subgraph is currently the target of a
     * <code>REFERENCE</code> property located in this workspace but outside the specified item's
     * subgraph and the current <code>Session</code> <i>does not</i> have read access to that
     * <code>REFERENCE</code> property.
     * 
     * @param absPath
     *            the absolute path of the item to be removed.
     * 
     * @throws VersionException
     *             if the parent node of the item at absPath is read-only due to a checked-in node
     *             and this implementation performs this validation immediately.
     * @throws LockException
     *             if a lock prevents the removal of the specified item and this implementation
     *             performs this validation immediately.
     * @throws ConstraintViolationException
     *             if removing the specified item would violate a node type or
     *             implementation-specific constraint and this implementation performs this
     *             validation immediately.
     * @throws PathNotFoundException
     *             if no accessible item is found at <code>absPath</code> and this implementation
     *             performs this validation immediately.
     * @throws AccessDeniedException
     *             if the specified item or an item in its subgraph is currently the target of a
     *             <code>REFERENCE</code> property located in this workspace but outside the
     *             specified item's subgraph and the current <code>Session</code> <i>does not</i>
     *             have read access to that <code>REFERENCE</code> property and this implementation
     *             performs this validation immediately.
     * @throws RepositoryException
     *             if another error occurs.
     * @see Item#remove()
     * @since JCR 2.0
     */
    public void removeItem(String absPath);

    /**
     * Returns <code>true</code> if this <code>Session</code> has permission to perform the
     * specified actions at the specified <code>absPath</code> and <code>false</code> otherwise.
     * <p>
     * The <code>actions</code> parameter is a comma separated list of action strings. The following
     * action strings are defined:
     * <ul>
     * <li> {@link #ACTION_ADD_NODE <code>add_node</code>}: If <code>hasPermission(path,
     * "add_node")</code> returns <code>true</code>, then this <code>Session</code> has permission
     * to add a node at <code>path</code>.</li>
     * <li> {@link #ACTION_SET_PROPERTY <code>set_property</code>}: If
     * <code>hasPermission(path, "set_property")</code> returns <code>true</code>, then this
     * <code>Session</code> has permission to set (add or change) a property at <code>path</code>.</li>
     * <li> {@link #ACTION_REMOVE <code>remove</code>}: If <code>hasPermission(path,
     * "remove")</code> returns <code>true</code>, then this <code>Session</code> has permission to
     * remove an item at <code>path</code>.</li>
     * <li> {@link #ACTION_READ <code>read</code>}: If <code>hasPermission(path, "read")</code>
     * returns <code>true</code>, then this <code>Session</code> has permission to retrieve (and
     * read the value of, in the case of a property) an item at <code>path</code>.</li>
     * </ul>
     * When more than one action is specified in the <code>actions</code> parameter, this method
     * will only return <code>true</code> if this <code>Session</code> has permission to perform
     * <i>all</i> of the listed actions at the specified path.
     * <p>
     * The information returned through this method will only reflect the access control status
     * (both JCR defined and implementation-specific) and not other restrictions that may exist,
     * such as node type constraints. For example, even though <code>hasPermission</code> may
     * indicate that a particular <code>Session</code> may add a property at <code>/A/B/C</code>,
     * the node type of the node at <code>/A/B</code> may prevent the addition of a property called
     * <code>C</code>.
     * 
     * @param absPath
     *            an absolute path.
     * @param actions
     *            a comma separated list of action strings.
     * @return <code>true</code> if this <code>Session</code> has permission to perform the
     *         specified actions at the specified <code>absPath</code>.
     * @throws RepositoryException
     *             if an error occurs.
     * @since JCR 2.0
     */
    public boolean hasPermission(String absPath, String actions);

    /**
     * Checks whether an operation can be performed given as much context as can be determined by
     * the repository, including:
     * <ul>
     * <li>Permissions granted to the current user, including access control privileges.</li>
     * <li>
     * Current state of the target object (reflecting locks, checkin/checkout status, retention and
     * hold status etc.).</li>
     * <li>Repository capabilities.</li>
     * <li>Node type-enforced restrictions.</li>
     * <li>
     * Repository configuration-specific restrictions.</li>
     * </ul>
     * The implementation of this method is best effort: returning <code>false</code> guarantees
     * that the operation cannot be performed, but returning <code>true</code> does not guarantee
     * the opposite. The repository implementation should use this to give priority to performance
     * over completeness. An exception should be thrown only for important failures such as loss of
     * connectivity to the back-end.
     * <p>
     * The implementation of this method is best effort: returning false guarantees that the
     * operation cannot be performed, but returning true does not guarantee the opposite.
     * <p>
     * The <code>methodName</code> parameter identifies the method in question by its name as
     * defined in the Javadoc.
     * <p>
     * The <code>target</code> parameter identifies the object on which the specified method is
     * called.
     * <p>
     * The <code>arguments</code> parameter contains an array of type <code>Object</code> object
     * consisting of the arguments to be passed to the method in question. In cases where a
     * parameter is a Java primitive type it must be converted to its corresponding Java object
     * form.
     * <p>
     * For example, given a <code>Session</code> <code>S</code> and <code>Node</code> <code>N</code>
     * then
     * <p>
     * <code>boolean b = S.hasCapability("addNode", N, new Object[]{"foo"});</code>
     * <p>
     * will result in <code>b == false</code> if a child node called <code>foo</code> cannot be
     * added to the node <code>N</code> within the session <code>S</code>.
     * 
     * @param methodName
     *            the nakme of the method.
     * @param target
     *            the target object of the operation.
     * @param arguments
     *            the arguments of the operation.
     * @return <code>false</code> if the operation cannot be performed, <code>true</code> if the
     *         operation can be performed or if the repository cannot determine whether the
     *         operation can be performed.
     * @throws RepositoryException
     *             if an error occurs
     * @since JCR 2.0
     */
    public boolean hasCapability(String methodName, Object target, Object[] arguments);


    /**
     * Returns the access control manager for this <code>Session</code>.
     * 
     * @return the access control manager for this <code>Session</code>
     * @throws UnsupportedRepositoryOperationException
     *             if access control is not supported.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public AccessControlManager getAccessControlManager();

    /**
     * Returns the retention and hold manager for this <code>Session</code>.
     * 
     * @return the retention manager for this <code>Session</code>.
     * @throws UnsupportedRepositoryOperationException
     *             if retention and hold are not supported.
     * @throws RepositoryException
     *             if another error occurs.
     * @since JCR 2.0
     */
    public RetentionManager getRetentionManager();
}