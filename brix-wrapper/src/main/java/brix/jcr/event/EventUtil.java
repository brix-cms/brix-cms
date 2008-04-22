package brix.jcr.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.event.wrapper.WrapperAccessor;
import brix.jcr.exception.JcrException;

public class EventUtil
{

    public static void raiseSaveEvent(Node node)
    {
        try
        {
            JcrSession session = JcrSession.Wrapper.wrap(node.getSession(), null);
            JcrNode wrapped = JcrNode.Wrapper.wrap(node, session);
            raiseSaveEvent(wrapped);
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
    }

    public static void raiseSaveEvent(JcrNode node)
    {
        Event event = new EventImpl(node);

        synchronized (listeners)
        {
            for (SaveEventListener listener : listeners)
            {
                listener.onEvent(new Iterator(event));
            }
        }
    }

    private final static List<SaveEventListener> listeners = Collections
            .synchronizedList(new ArrayList<SaveEventListener>());

    public static void registerSaveEventListener(SaveEventListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Ensure that calling checkin and save on nodes within the session raises the save event.
     * 
     * @param session
     * @return
     */
    public static Session wrapSession(Session session)
    {
        return WrapperAccessor.wrap(session);
    }

    public static Session unwrapSession(Session session)
    {
        return WrapperAccessor.unwrap(session);
    }

    private static class Iterator implements EventIterator
    {

        private Event event;

        public Iterator(Event event)
        {
            this.event = event;
        }

        public Event nextEvent()
        {
            Event res = event;
            event = null;
            return res;
        }

        public long getPosition()
        {
            return 0;
        }

        public long getSize()
        {
            return -1;
        }

        public void skip(long skipNum)
        {

        }

        public boolean hasNext()
        {
            return event != null;
        }

        public Object next()
        {
            return nextEvent();
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    };

    private static class EventImpl implements SaveEvent
    {

        private final JcrNode node;

        public EventImpl(JcrNode node)
        {
            this.node = node;
        }

        public JcrNode getNode()
        {
            return node;
        }

        public String getPath() throws RepositoryException
        {
            return node.getPath();
        }

        public int getType()
        {
            return SaveEvent.NODE_SAVE;
        }

        public String getUserID()
        {
            return node.getSession().getUserID();
        }

    };

}
