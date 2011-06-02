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

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

class VersionHistoryWrapper extends NodeWrapper implements VersionHistory {
    public static VersionHistoryWrapper wrap(VersionHistory history, SessionWrapper session) {
        if (history == null) {
            return null;
        } else {
            return new VersionHistoryWrapper(history, session);
        }
    }

    private VersionHistoryWrapper(VersionHistory delegate, SessionWrapper session) {
        super(delegate, session);
    }



    public String getVersionableUUID() throws RepositoryException {
        return getDelegate().getVersionableIdentifier();
    }

    public String getVersionableIdentifier() throws RepositoryException {
        return getDelegate().getVersionableIdentifier();
    }

    public Version getRootVersion() throws RepositoryException {
        return VersionWrapper.wrap(getDelegate().getRootVersion(), getSessionWrapper());
    }

    public VersionIterator getAllLinearVersions() throws RepositoryException {
        return VersionIteratorWrapper.wrap(getDelegate().getAllLinearVersions(),
                getSessionWrapper());
    }

    public VersionIterator getAllVersions() throws RepositoryException {
        return VersionIteratorWrapper.wrap(getDelegate().getAllVersions(), getSessionWrapper());
    }

    public NodeIterator getAllLinearFrozenNodes() throws RepositoryException {
        return NodeIteratorWrapper.wrap(getDelegate().getAllLinearFrozenNodes(),
                getSessionWrapper());
    }

    public NodeIterator getAllFrozenNodes() throws RepositoryException {
        return NodeIteratorWrapper.wrap(getDelegate().getAllFrozenNodes(), getSessionWrapper());
    }

    public Version getVersion(String versionName) throws RepositoryException {
        return VersionWrapper.wrap(getDelegate().getVersion(versionName), getSessionWrapper());
    }

    public Version getVersionByLabel(String label) throws RepositoryException {
        return VersionWrapper.wrap(getDelegate().getVersionByLabel(label), getSessionWrapper());
    }

    public void addVersionLabel(String versionName, String label, boolean moveLabel)
            throws RepositoryException {
        getDelegate().addVersionLabel(versionName, label, moveLabel);
    }

    public void removeVersionLabel(String label) throws RepositoryException {
        getDelegate().removeVersionLabel(label);
    }

    public boolean hasVersionLabel(String label) throws RepositoryException {
        return getDelegate().hasVersionLabel(label);
    }

    public boolean hasVersionLabel(Version version, String label) throws RepositoryException {
        return getDelegate().hasVersionLabel(unwrap(version), label);
    }

    public String[] getVersionLabels() throws RepositoryException {
        return getDelegate().getVersionLabels();
    }

    public String[] getVersionLabels(Version version) throws RepositoryException {
        return getDelegate().getVersionLabels(version);
    }

    public void removeVersion(String versionName) throws RepositoryException {
        getDelegate().removeVersion(versionName);
    }

    @Override
    public VersionHistory getDelegate() {
        return (VersionHistory) super.getDelegate();
    }
}
