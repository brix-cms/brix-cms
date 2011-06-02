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
 * Abstract event for events with node.
 *
 * @author Matej Knopp
 */
abstract class NodeEvent extends Event {
    final Node node;

    NodeEvent(Node node) {
        this.node = node;
    }

    Node getNode() {
        return node;
    }

    @Override
    boolean isAffected(String path) throws RepositoryException {
        String currentPath = getNode().getPath();
        return currentPath.startsWith(path);
    }

    @Override
    Event onNewEvent(Event event, QueueCallback queueCallback) throws RepositoryException {
        // if this event's node or some of it's parent is being removed this
        // event should be removed as well
        if (event instanceof BeforeRemoveNodeEvent) {
            BeforeRemoveNodeEvent e = (BeforeRemoveNodeEvent) event;
            if (getNode().getPath().startsWith(e.getNode().getPath())) {
                return null;
            }
        }
        return this;
    }
}