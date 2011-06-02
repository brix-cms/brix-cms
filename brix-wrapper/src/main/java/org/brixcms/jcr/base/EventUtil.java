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

package org.brixcms.jcr.base;

import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.base.wrapper.WrapperAccessor;
import org.brixcms.jcr.exception.JcrException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventUtil {
    private final static List<SaveEventListener> listeners = Collections
            .synchronizedList(new ArrayList<SaveEventListener>());

    public static void raiseSaveEvent(Node node) {
        try {
            JcrSession session = JcrSession.Wrapper.wrap(node.getSession(), null);
            JcrNode wrapped = JcrNode.Wrapper.wrap(node, session);
            raiseSaveEvent(wrapped);
        } catch (RepositoryException e) {
            throw new JcrException(e);
        }
    }

    public static void raiseSaveEvent(JcrNode node) {
        Event event = new EventImpl(node);

        synchronized (listeners) {
            for (SaveEventListener listener : listeners) {
                listener.onEvent(new Iterator(event));
            }
        }
    }

    public static void registerSaveEventListener(SaveEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Ensure that calling checkin and save on nodes within the session raises the save event.
     *
     * @param session
     * @return
     */
    public static BrixSession wrapSession(Session session) {
        return WrapperAccessor.wrap(session);
    }

    public static Session unwrapSession(Session session) {
        return WrapperAccessor.unwrap(session);
    }

    private static class Iterator implements EventIterator {
        private Event event;

        public Iterator(Event event) {
            this.event = event;
        }

        public Event nextEvent() {
            Event res = event;
            event = null;
            return res;
        }

        public long getPosition() {
            return 0;
        }

        public long getSize() {
            return -1;
        }

        public void skip(long skipNum) {

        }

        public boolean hasNext() {
            return event != null;
        }

        public Object next() {
            return nextEvent();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class EventImpl implements SaveEvent {
        private final JcrNode node;
        private long timestamp = System.currentTimeMillis();

        public EventImpl(JcrNode node) {
            this.node = node;
        }

        public JcrNode getNode() {
            return node;
        }

        public String getPath() throws RepositoryException {
            return node.getPath();
        }

        public int getType() {
            return SaveEvent.NODE_SAVE;
        }

        public String getUserID() {
            return node.getSession().getUserID();
        }

        public long getDate() throws RepositoryException {
            return timestamp;
        }

        public String getIdentifier() throws RepositoryException {
            return node.getIdentifier();
        }

        public Map<?, ?> getInfo() throws RepositoryException {
            throw new UnsupportedOperationException();
        }

        public String getUserData() throws RepositoryException {
            throw new UnsupportedOperationException();
        }
    }

    ;
}
