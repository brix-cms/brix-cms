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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import java.util.Calendar;


class VersionWrapper extends NodeWrapper implements javax.jcr.version.Version {
    public static VersionWrapper wrap(Version delegate, SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            return new VersionWrapper(delegate, session);
        }
    }

    public static VersionWrapper[] wrap(Version delegate[], SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            VersionWrapper result[] = new VersionWrapper[delegate.length];
            for (int i = 0; i < delegate.length; ++i) {
                result[i] = wrap(delegate[i], session);
            }
            return result;
        }
    }

    private VersionWrapper(Version delegate, SessionWrapper session) {
        super(delegate, session);
    }


    public VersionHistory getContainingHistory() throws RepositoryException {
        return VersionHistoryWrapper
                .wrap(getDelegate().getContainingHistory(), getSessionWrapper());
    }

    public Calendar getCreated() throws RepositoryException {
        return getDelegate().getCreated();
    }

    public Version getLinearSuccessor() throws RepositoryException {
        return VersionWrapper.wrap(getDelegate().getLinearSuccessor(), getSessionWrapper());
    }

    public Version[] getSuccessors() throws RepositoryException {
        return wrap(getDelegate().getSuccessors(), getSessionWrapper());
    }

    public Version getLinearPredecessor() throws RepositoryException {
        return VersionWrapper.wrap(getDelegate().getLinearPredecessor(), getSessionWrapper());
    }

    public Version[] getPredecessors() throws RepositoryException {
        return wrap(getDelegate().getPredecessors(), getSessionWrapper());
    }

    public Node getFrozenNode() throws RepositoryException {
        return NodeWrapper.wrap(getDelegate().getFrozenNode(), getSessionWrapper());
    }

    @Override
    public Version getDelegate() {
        return (Version) super.getDelegate();
    }
}
