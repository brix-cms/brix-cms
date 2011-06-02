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

package org.brixcms.jcr;

import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.registry.ExtensionPoint;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Produces wrappers for various {@link Node}s. This factory is usually used to automatically wrap a {@link Node}
 * retrieved from {@link JcrSession} with a {@link JcrSession} or it's subclass. For Example:
 * <p/>
 * <pre>
 * PersonNode node = (PersonNode) session.getRootNode().getNode(&quot;person&quot;);
 * node.setFirstName(&quot;Bob&quot;);
 * </pre>
 * <p/>
 * rather then something like this:
 * <p/>
 * <pre>
 * JcrNode node = session.getRootNode().getNode(&quot;person&quot;);
 * PersonNodeAdapter adapter = new PersonNodeAdapter(node);
 * adapter.setFirstName(&quot;Bob&quot;);
 * </pre>
 * <p/>
 * Since most of the time a node is wrapper based on its type the {@link #initializeRepository(Repository)} method can
 * be used for registering a node type.
 *
 * @author igor.vaynberg
 */
public abstract class JcrNodeWrapperFactory implements RepositoryInitializer {
    /**
     * Extension point for factory implementations
     *
     * @author igor.vaynberg
     */
    public static final ExtensionPoint<JcrNodeWrapperFactory> POINT = new ExtensionPoint<JcrNodeWrapperFactory>() {
        public Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        public String getUuid() {
            return JcrNodeWrapperFactory.class.getName();
        }
    };


    /**
     * {@inheritDoc}
     * <p/>
     * Called when the repository is initialized. For example, this call can be used to register any node types for
     * nodes this factory can wrap.
     *
     * @param session
     */
    public void initializeRepository(Brix brix, Session session) throws RepositoryException {
        // noop
    }

    /**
     * Checks if this factory can wrap the node. The node instance is a simple {@link JcrNode} wrapper around the
     * original {@link Node}
     *
     * @param node
     * @return true if this factory can wrap the node
     */
    public abstract boolean canWrap(Brix brix, JcrNode node);

    /**
     * Wraps the node with a subclass.
     * <p/>
     * <p/>
     * Passed as the argument is the "original" {@link Node} instance. This is to make sure that each instance is
     * wrapped at most once. Wrapping a single {@link Node} instance with multiple nested wrappers might lead to
     * problems with exceptions translating.
     *
     * @param node
     * @param session
     * @return wrapper
     */
    public abstract JcrNode wrap(Brix brix, Node node, JcrSession session);
}
