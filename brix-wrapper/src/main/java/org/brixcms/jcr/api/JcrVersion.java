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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import java.util.Calendar;

/**
 * @author Matej Knopp
 * @author igor.vaynberg
 */
public interface JcrVersion extends Version, JcrNode {

// --------------------- Interface JcrItem ---------------------
    public Version getDelegate();


    public JcrVersionHistory getContainingHistory();

    public Calendar getCreated();

    public JcrVersion[] getSuccessors();

    public JcrVersion[] getPredecessors();

    /**
     * Returns the frozen node of this version.
     *
     * @return a <code>Node</code> object
     * @throws RepositoryException if an error occurs.
     * @since JCR 2.0
     */
    public Node getFrozenNode();

    public static class Wrapper {
        public static JcrVersion wrap(Version delegate, JcrSession session) {
            return WrapperAccessor.JcrVersionWrapper.wrap(delegate, session);
        }

        public static JcrVersion[] wrap(Version delegate[], JcrSession session) {
            return WrapperAccessor.JcrVersionWrapper.wrap(delegate, session);
        }
    }
}