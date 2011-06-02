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
 * Event for new node being added.
 *
 * @author Matej Knopp
 */
public class AddNodeEvent extends NodeEvent {
    AddNodeEvent(Node node) {
        super(node);
    }

    public Node getNewNode() {
        return super.getNode();
    }

    @Override
    Event onNewEvent(Event event, QueueCallback queueCallback) throws RepositoryException {
        if (event instanceof BeforeRemoveNodeEvent) {
            BeforeRemoveNodeEvent e = (BeforeRemoveNodeEvent) event;
            if (getNewNode().getPath().equals(e.getNode().getPath())) {
                // we are removing same node as we have added. Thus it is
                // not necessary to add the remove event to queue
                queueCallback.blockAddingEvent();
                return null;
            }
        }
        return super.onNewEvent(event, queueCallback);
    }
}