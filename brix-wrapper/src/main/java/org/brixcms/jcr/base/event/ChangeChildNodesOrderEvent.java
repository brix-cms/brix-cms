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

package org.brixcms.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Event for node children order having changed.
 *
 * @author Matej Knopp
 */
public class ChangeChildNodesOrderEvent extends NodeEvent {
    ChangeChildNodesOrderEvent(Node node) {
        super(node);
    }

    @Override
    public Node getNode() {
        return super.getNode();
    }

    @Override
    Event onNewEvent(Event event, QueueCallback callback) throws RepositoryException {
        if (event instanceof ChangeChildNodesOrderEvent) {
            ChangeChildNodesOrderEvent e = (ChangeChildNodesOrderEvent) event;
            if (e.getNode().getPath().equals(getNode().getPath())) {
                // remove current event, will get replaced by the new one
                return null;
            }
        }
        return super.onNewEvent(event, callback);
    }
}