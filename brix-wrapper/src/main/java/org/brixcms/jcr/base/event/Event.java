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

import javax.jcr.RepositoryException;

/**
 * Base event class. All events have to be subclasses of Event.
 *
 * @author Matej Knopp
 */
public abstract class Event {
    /**
     * Returns true if this event is affected by the specified path. The path is usually path of node being saved and this
     * method should check if this event is event either for the node with given path or it's child node (doesn't have to
     * be immediate child).
     *
     * @param path
     * @return
     * @throws RepositoryException
     */
    boolean isAffected(String path) throws RepositoryException {
        return false;
    }

    /**
     * Notifies this event that another event is about to be added to the queue.
     *
     * @param event
     * @param queueCallback allows to block adding the other event to the queue
     * @return Event that this event should be replaced with (or <code>this</code>) if it shouldn't be replaced by other
     *         event. <code>null</code> if this event should be removed from queue.
     * @throws RepositoryException
     */
    Event onNewEvent(Event event, QueueCallback queueCallback) throws RepositoryException {
        return this;
    }

    /**
     * Allows to add different event to queue instead of this one.
     *
     * @return
     * @throws RepositoryException
     */
    Event transformBeforeAddingToQueue() throws RepositoryException {
        return this;
    }

    /**
     * Allows event to block adding other events to the queue.
     *
     * @author Matej Knopp
     */
    interface QueueCallback {
        public void blockAddingEvent();
    }
}