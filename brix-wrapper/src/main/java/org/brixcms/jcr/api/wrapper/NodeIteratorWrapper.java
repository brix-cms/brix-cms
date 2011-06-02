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
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.api.JcrSession;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RangeIterator;

/**
 * @author Matej Knopp
 */
class NodeIteratorWrapper extends RangeIteratorWrapper implements JcrNodeIterator {
    public static JcrNodeIterator wrap(NodeIterator delegate, JcrSession session) {
        if (delegate == null) {
            return null;
        } else {
            return new NodeIteratorWrapper(delegate, session);
        }
    }

    protected NodeIteratorWrapper(RangeIterator delegate, JcrSession session) {
        super(delegate, session);
    }



    @Override
    public Object next() {
        return JcrNode.Wrapper.wrap((Node) getDelegate().next(), getJcrSession());
    }

    @Override
    public NodeIterator getDelegate() {
        return (NodeIterator) super.getDelegate();
    }


    public JcrNode nextNode() {
        return JcrNode.Wrapper.wrap(getDelegate().nextNode(), getJcrSession());
    }
}
