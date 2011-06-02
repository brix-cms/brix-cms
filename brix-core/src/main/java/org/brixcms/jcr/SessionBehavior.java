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
import org.brixcms.exception.BrixException;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.api.JcrSession.Behavior;
import org.brixcms.jcr.api.wrapper.NodeWrapper;
import org.brixcms.jcr.base.EventUtil;
import org.brixcms.jcr.exception.JcrException;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.jcr.wrapper.ResourceNode;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collection;

public class SessionBehavior implements Behavior {
    private final Brix brix;

    public SessionBehavior(Brix brix) {
        this.brix = brix;
    }



    public JcrNode wrap(Node node, JcrSession session) {
        if (node instanceof JcrNode) {
            return (JcrNode) node;
        }

        JcrNode n = new NodeWrapper(node, session);

        Collection<JcrNodeWrapperFactory> factories = brix.getConfig().getRegistry().lookupCollection(
                JcrNodeWrapperFactory.POINT);

        for (JcrNodeWrapperFactory factory : factories) {
            if (factory.canWrap(brix, n)) {
                return factory.wrap(brix, node, session);
            }
        }

        if (ResourceNode.FACTORY.canWrap(brix, n)) {
            return ResourceNode.FACTORY.wrap(brix, node, session);
        }

        return new BrixNode(node, session);
    }

    public void nodeSaved(JcrNode node) {
        EventUtil.raiseSaveEvent(node);
    }

    public void handleException(Exception e) {
        if (e instanceof RepositoryException) {
            throw new JcrException((RepositoryException) e);
        } else {
            throw new BrixException(e);
        }
    }
}
