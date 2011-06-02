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
 * This event is only passed to other event's {@link #onNewEvent(Event)} method. Not that this event will never be added
 * to the queue (because the Node becomes invalid after deletion). Instead {@link RemoveNodeEvent} will be added to the
 * queue.
 *
 * @author Matej Knopp
 */
public class BeforeRemoveNodeEvent extends NodeEvent {
    BeforeRemoveNodeEvent(Node node) {
        super(node);
    }

    @Override
    public Event transformBeforeAddingToQueue() throws RepositoryException {
        return new RemoveNodeEvent(getNode());
    }

    @Override
    protected Node getNode() {
        return super.getNode();
    }
}