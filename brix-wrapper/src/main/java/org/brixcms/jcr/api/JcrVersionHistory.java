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

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrVersionHistory extends VersionHistory, JcrNode {

// --------------------- Interface JcrItem ---------------------
    public VersionHistory getDelegate();


    /**
     * @deprecated As of JCR 2.0, {@link #getVersionableIdentifier} should be used instead.
     */
    @Deprecated
    public String getVersionableUUID();

    /**
     * Returns the identifier of the versionable node for which this is the version history.
     *
     * @return the identifier of the versionable node for which this is the version history.
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public String getVersionableIdentifier();

    public JcrVersion getRootVersion();

    /**
     * This method returns an iterator over all the versions in the <i>line of descent</i> from the root version to that
     * base version within this history <i>that is bound to the workspace through which this <code>VersionHistory</code>
     * was accessed</i>.
     * <p/>
     * Within a version history <code>H</code>, <code>B</code> is the base version bound to workspace <code>W</code> if
     * and only if there exists a versionable node <code>N</code> in <code>W</code> whose version history is
     * <code>H</code> and <code>B</code> is the base version of <code>N</code>.
     * <p/>
     * The <i>line of descent</i> from version <code>V1</code> to <code>V2</code>, where <code>V2</code> is a successor
     * of <code>V1</code>, is the ordered list of versions starting with <code>V1</code> and proceeding through each
     * direct successor to <code>V2</code>.
     * <p/>
     * The versions are returned in order of creation date, from oldest to newest.
     * <p/>
     * Note that in a simple versioning repository the behavior of this method is equivalent to returning all versions
     * in the version history in order from oldest to newest.
     *
     * @return a <code>VersionIterator</code> object.
     * @throws RepositoryException if an error occurs.
     */
    public VersionIterator getAllLinearVersions();

    public JcrVersionIterator getAllVersions();

    /**
     * This method returns all the frozen nodes of all the versions in this version history in the same order as {@link
     * #getAllLinearVersions}.
     *
     * @return a <code>NodeIterator</code> object.
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public NodeIterator getAllLinearFrozenNodes();

    /**
     * Returns an iterator over all the frozen nodes of all the versions of this version history. Under simple
     * versioning the order of the returned nodes will be the order of their creation. Under full versioning the order
     * is implementation-dependent.
     *
     * @return a <code>NodeIterator</code> object.
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public NodeIterator getAllFrozenNodes();

    public JcrVersion getVersion(String versionName);

    public Version getVersionByLabel(String label);

    public void addVersionLabel(String versionName, String label, boolean moveLabel);

    public void removeVersionLabel(String label);

    public boolean hasVersionLabel(String label);

    public boolean hasVersionLabel(Version version, String label);

    public String[] getVersionLabels();

    public String[] getVersionLabels(Version version);

    public void removeVersion(String versionName);

    public static class Wrapper {
        public static JcrVersionHistory wrap(VersionHistory delegate, JcrSession session) {
            return WrapperAccessor.JcrVersionHistoryWrapper.wrap(delegate, session);
        }
    }
}