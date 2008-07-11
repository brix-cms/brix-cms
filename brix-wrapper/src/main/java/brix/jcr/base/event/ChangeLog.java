package brix.jcr.base.event;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * ChangeLog keep tracks of events happening in a single {@link Session}. At
 * certain points (usually when Item#save() or {@link Session#save()} is about
 * to be called) ChangeLog can provide normalized list of events that have
 * happened before the point.
 * <p>
 * The session must add proper events to the {@link ChangeLog} using
 * {@link #addEvent(Event)} and {@link ChangeLog} will make sure that the events
 * will be normalized (i.e. redundant events will be removed, etc).
 * 
 * @see #addEvent(Event)
 * @see #removeAndGetAffectedEvents(Node)
 * 
 * @author Matej Knopp
 */
public class ChangeLog
{

	public ChangeLog()
	{

	}

	private List<Event> events = new ArrayList<Event>();

	/**
	 * Adds the event to the event queue.
	 * 
	 * @param event
	 * @throws RepositoryException
	 */
	public void addEvent(Event event) throws RepositoryException
	{
		final boolean blockAddingEvent[] = { false };
		for (int i = 0; i < events.size(); ++i)
		{
			Event e = events.get(i);
			Event.QueueCallback callback = new Event.QueueCallback()
			{
				public void blockAddingEvent()
				{
					blockAddingEvent[0] = true;
				}

			};
			events.set(i, e.onNewEvent(event, callback));
		}

		removeNullEvents();

		if (blockAddingEvent[0] == false)
		{
			Event transformed = event.transformBeforeAddingToQueue();
			if (transformed != null)
			{
				events.add(transformed);
			}
		}
	}

	/**
	 * Shrinks the queue removing null events.
	 */
	private void removeNullEvents()
	{
		// remove null events
		List<Event> newList = new ArrayList<Event>(events.size());
		for (Event e : events)
		{
			if (e != null)
			{
				newList.add(e);
			}
		}

		events = newList;
	}

	/**
	 * Removes events affected by this item (events concerning the path or any
	 * of it's children) and returns them. If path is<code>null</code> all events
	 * are returned.
	 * 
	 * @param path
	 * @return
	 * @throws RepositoryException
	 */
	public List<Event> removeAndGetAffectedEvents(String path) throws RepositoryException
	{
		List<Event> result;
		if (path == null)
		{
			result = events;
			events = new ArrayList<Event>();
		}
		else
		{
			result = new ArrayList<Event>();
			for (int i = 0; i < events.size(); ++i)
			{
				Event e = events.get(i);
				if (e.isAffected(path))
				{
					result.add(e);
					events.set(i, null);
				}
			}
			removeNullEvents();
		}
		return result;
	}

}
