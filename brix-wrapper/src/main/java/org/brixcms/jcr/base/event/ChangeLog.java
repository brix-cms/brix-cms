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
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;

/**
 * ChangeLog keep tracks of events happening in a single {@link Session}. At certain points (usually when Item#save() or
 * {@link Session#save()} is about to be called) ChangeLog can provide normalized list of events that have happened
 * before the point.
 * <p/>
 * The session must add proper events to the {@link ChangeLog} using {@link #addEvent(Event)} and {@link ChangeLog} will
 * make sure that the events will be normalized (i.e. redundant events will be removed, etc).
 *
 * @author Matej Knopp
 * @see #addEvent(Event)
 * @see #removeAndGetAffectedEvents(Node)
 */
public class ChangeLog {
    private List<Event> events = new ArrayList<Event>();

    public ChangeLog() {

    }

    /**
     * Adds the event to the event queue.
     *
     * @param event
     * @throws RepositoryException
     */
    public void addEvent(Event event) throws RepositoryException {
        final boolean blockAddingEvent[] = {false};
        for (int i = 0; i < events.size(); ++i) {
            Event e = events.get(i);
            Event.QueueCallback callback = new Event.QueueCallback() {
                public void blockAddingEvent() {
                    blockAddingEvent[0] = true;
                }
            };
            events.set(i, e.onNewEvent(event, callback));
        }

        removeNullEvents();

        if (blockAddingEvent[0] == false) {
            Event transformed = event.transformBeforeAddingToQueue();
            if (transformed != null) {
                events.add(transformed);
            }
        }
    }

    /**
     * Shrinks the queue removing null events.
     */
    private void removeNullEvents() {
        // remove null events
        List<Event> newList = new ArrayList<Event>(events.size());
        for (Event e : events) {
            if (e != null) {
                newList.add(e);
            }
        }

        events = newList;
    }

    /**
     * Removes events affected by this item (events concerning the path or any of it's children) and returns them. If path
     * is<code>null</code> all events are returned.
     *
     * @param path
     * @return
     * @throws RepositoryException
     */
    public List<Event> removeAndGetAffectedEvents(String path) throws RepositoryException {
        List<Event> result;
        if (path == null) {
            result = events;
            events = new ArrayList<Event>();
        } else {
            result = new ArrayList<Event>();
            for (int i = 0; i < events.size(); ++i) {
                Event e = events.get(i);
                if (e.isAffected(path)) {
                    result.add(e);
                    events.set(i, null);
                }
            }
            removeNullEvents();
        }
        return result;
    }
}
