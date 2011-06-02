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
import javax.jcr.NodeIterator;

class NodeIteratorWrapper extends BaseWrapper<NodeIterator> implements NodeIterator {
    public static NodeIteratorWrapper wrap(NodeIterator delegate, SessionWrapper session) {
        if (delegate == null) {
            return null;
        } else {
            return new NodeIteratorWrapper(delegate, session);
        }
    }

    private NodeIteratorWrapper(NodeIterator delegate, SessionWrapper session) {
        super(delegate, session);
    }



    public boolean hasNext() {
        return getDelegate().hasNext();
    }

    public Object next() {
        return NodeWrapper.wrap((Node) getDelegate().next(), getSessionWrapper());
    }

    public void remove() {
        getDelegate().remove();
    }

    public Node nextNode() {
        return NodeWrapper.wrap(getDelegate().nextNode(), getSessionWrapper());
    }


    public void skip(long skipNum) {
        getDelegate().skip(skipNum);
    }

    public long getSize() {
        return getDelegate().getSize();
    }

    public long getPosition() {
        return getDelegate().getPosition();
    }
}
