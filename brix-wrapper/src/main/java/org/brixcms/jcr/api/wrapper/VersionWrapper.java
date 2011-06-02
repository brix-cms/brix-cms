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

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrVersion;
import org.brixcms.jcr.api.JcrVersionHistory;

import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import java.util.Calendar;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
class VersionWrapper extends NodeWrapper implements JcrVersion {
    public static JcrVersion wrap(Version delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new VersionWrapper(delegate, session);
        }
    }

    public static JcrVersion[] wrap(Version delegate[], JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            JcrVersion res[] = new JcrVersion[delegate.length];
            for (int i = 0; i < delegate.length; ++i) {
                res[i] = wrap(delegate[i], session);
            }
            return res;
        }
    }

    protected VersionWrapper(Version delegate, JcrSession session) {
        super(delegate, session);
    }


    @Override
    public Version getDelegate() {
        return (Version) super.getDelegate();
    }


    public JcrVersion getLinearSuccessor() throws RepositoryException {
        return executeCallback(new Callback<JcrVersion>() {
            public JcrVersion execute() throws Exception {
                return JcrVersion.Wrapper.wrap(getDelegate().getLinearSuccessor(), getSession());
            }
        });
    }

    public JcrVersion getLinearPredecessor() throws RepositoryException {
        return executeCallback(new Callback<JcrVersion>() {
            public JcrVersion execute() throws Exception {
                return JcrVersion.Wrapper.wrap(getDelegate().getLinearPredecessor(),
                        getJcrSession());
            }
        });
    }

    public JcrVersionHistory getContainingHistory() {
        return executeCallback(new Callback<JcrVersionHistory>() {
            public JcrVersionHistory execute() throws Exception {
                return JcrVersionHistory.Wrapper.wrap(getDelegate().getContainingHistory(),
                        getJcrSession());
            }
        });
    }

    public Calendar getCreated() {
        return executeCallback(new Callback<Calendar>() {
            public Calendar execute() throws Exception {
                return getDelegate().getCreated();
            }
        });
    }

    public JcrVersion[] getSuccessors() {
        return executeCallback(new Callback<JcrVersion[]>() {
            public JcrVersion[] execute() throws Exception {
                return JcrVersion.Wrapper.wrap(getDelegate().getSuccessors(), getJcrSession());
            }
        });
    }

    public JcrVersion[] getPredecessors() {
        return executeCallback(new Callback<JcrVersion[]>() {
            public JcrVersion[] execute() throws Exception {
                return JcrVersion.Wrapper.wrap(getDelegate().getPredecessors(), getJcrSession());
            }
        });
    }

    public JcrNode getFrozenNode() {
        return executeCallback(new Callback<JcrNode>() {
            public JcrNode execute() throws Exception {
                return JcrNode.Wrapper.wrap(getDelegate().getFrozenNode(), getJcrSession());
            }
        });
    }
}
