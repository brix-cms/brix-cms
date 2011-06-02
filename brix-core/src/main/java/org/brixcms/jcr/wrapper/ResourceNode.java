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

package org.brixcms.jcr.wrapper;

import org.brixcms.Brix;
import org.brixcms.jcr.JcrNodeWrapperFactory;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;

import javax.jcr.Node;

/**
 * Wrapper for file nodes that are not wrapped by any other wrapper.
 *
 * @author Matej Knopp
 */
public class ResourceNode extends BrixFileNode {
    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory() {
        @Override
        public boolean canWrap(Brix brix, JcrNode node) {
            return isFileNode(node);
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session) {
            return new ResourceNode(node, session);
        }
    };

    private static final String REQUIRED_PROTOCOL = "brix:requiredProtocol";

    public ResourceNode(Node delegate, JcrSession session) {
        super(delegate, session);
    }

    @Override
    public Protocol getRequiredProtocol() {
        if (!hasProperty(REQUIRED_PROTOCOL)) {
            return Protocol.PRESERVE_CURRENT;
        } else {
            return Protocol.valueOf(getProperty(REQUIRED_PROTOCOL).getString());
        }
    }

    @Override
    public String getUserVisibleType() {
        return "Resource";
    }

    public void setRequiredProtocol(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("Argument 'protocol' may not be null.");
        }
        setProperty(REQUIRED_PROTOCOL, protocol.toString());
    }
}
