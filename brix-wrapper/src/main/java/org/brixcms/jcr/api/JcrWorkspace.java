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

package org.brixcms.jcr.api;

import org.brixcms.jcr.api.wrapper.WrapperAccessor;
import org.xml.sax.ContentHandler;

import javax.jcr.AccessDeniedException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.io.InputStream;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrWorkspace extends Workspace {


    public JcrSession getSession();

    public String getName();

    public void copy(String srcAbsPath, String destAbsPath);

    public void copy(String srcWorkspace, String srcAbsPath, String destAbsPath);

    public void clone(String srcWorkspace, String srcAbsPath, String destAbsPath,
                      boolean removeExisting);

    public void move(String srcAbsPath, String destAbsPath);

    /**
     * @deprecated As of JCR 2.0, {@link javax.jcr.version.VersionManager#restore} should be used instead.
     */
    @Deprecated
    public void restore(Version[] versions, boolean removeExisting);

    /**
     * Returns the <code>LockManager</code> object, through which locking methods are accessed.
     *
     * @return the <code>LockManager</code> object.
     * @throws UnsupportedRepositoryOperationException
     *                             if the implementation does not support locking.
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public LockManager getLockManager();

    public JcrQueryManager getQueryManager();

    public JcrNamespaceRegistry getNamespaceRegistry();

    public NodeTypeManager getNodeTypeManager();

    public ObservationManager getObservationManager();

    /**
     * Returns the <code>VersionManager</code> object.
     *
     * @return an <code>VersionManager</code> object.
     * @throws UnsupportedRepositoryOperationException
     *                             if the implementation does not support versioning.
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public VersionManager getVersionManager();

    public String[] getAccessibleWorkspaceNames();

    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior);

    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior);

    /**
     * Creates a new <code>Workspace</code> with the specified <code>name</code>. The new workspace is empty, meaning it
     * contains only root node.
     * <p/>
     * The new workspace can be accessed through a <code>login</code> specifying its name.
     *
     * @param name A <code>String</code>, the name of the new workspace.
     * @throws AccessDeniedException if the session through which this <code>Workspace</code> object was acquired does
     *                               not have permission to create the new workspace.
     * @throws UnsupportedRepositoryOperationException
     *                               if the repository does not support the creation of workspaces.
     * @throws RepositoryException   if another error occurs.
     * @since JCR 2.0
     */
    public void createWorkspace(String name);

    /**
     * Creates a new <code>Workspace</code> with the specified <code>name</code> initialized with a <code>clone</code>
     * of the content of the workspace <code>srcWorkspace</code>. Semantically, this method is equivalent to creating a
     * new workspace and manually cloning <code>srcWorkspace</code> to it; however, this method may assist some
     * implementations in optimizing subsequent <code>Node.update</code> and <code>Node.merge</code> calls between the
     * new workspace and its source.
     * <p/>
     * The new workspace can be accessed through a <code>login</code> specifying its name.
     *
     * @param name         A <code>String</code>, the name of the new workspace.
     * @param srcWorkspace The name of the workspace from which the new workspace is to be cloned.
     * @throws AccessDeniedException    if the session through which this <code>Workspace</code> object was acquired
     *                                  does not have sufficient access to create the new workspace.
     * @throws UnsupportedRepositoryOperationException
     *                                  if the repository does not support the creation of workspaces.
     * @throws NoSuchWorkspaceException is <code>srcWorkspace</code> does not exist.
     * @throws RepositoryException      if another error occurs.
     * @since JCR 2.0
     */
    public void createWorkspace(String name, String srcWorkspace);

    /**
     * Deletes the workspace with the specified <code>name</code> from the repository, deleting all content within it.
     *
     * @param name A <code>String</code>, the name of the workspace to be deleted.
     * @throws AccessDeniedException    if the session through which this <code>Workspace</code> object was acquired
     *                                  does not have sufficent access to remove the workspace.
     * @throws UnsupportedRepositoryOperationException
     *                                  if the repository does not support the removal of workspaces.
     * @throws NoSuchWorkspaceException is <code>srcWorkspace</code> does not exist.
     * @throws RepositoryException      if another error occurs.
     * @since JCR 2.0
     */
    public void deleteWorkspace(String name);

// -------------------------- OTHER METHODS --------------------------
    public Workspace getDelegate();

    public static class Wrapper {
        public static JcrWorkspace wrap(Workspace delegate, JcrSession session) {
            return WrapperAccessor.JcrWorkspaceWrapper.wrap(delegate, session);
        }
    }
}