package brix.jcr.base.event;

import javax.jcr.RepositoryException;

/**
 * Base event class. All events have to be subclasses of Event.
 * 
 * @author Matej Knopp
 */
public abstract class Event
{
	/**
	 * Allows event to block adding other events to the queue.
	 * 
	 * @author Matej Knopp
	 */
	interface QueueCallback
	{
		public void blockAddingEvent();
	};

	/**
	 * Notifies this event that another event is about to be added to the
	 * queue.
	 * 
	 * @param event
	 * @param queueCallback
	 *            allows to block adding the other event to the queue
	 * 
	 * @return Event that this event should be replaced with (or
	 *         <code>this</code>) if it shouldn't be replaced by other
	 *         event. <code>null</code> if this event should be removed
	 *         from queue.
	 * 
	 * @throws RepositoryException
	 */
	Event onNewEvent(Event event, QueueCallback queueCallback) throws RepositoryException
	{
		return this;
	}

	/**
	 * Allows to add different event to queue instead of this one.
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	Event transformBeforeAddingToQueue() throws RepositoryException
	{
		return this;
	}

	/**
	 * Returns true if this event is affected by the specified path. The
	 * path is usually path of node being saved and this method should check
	 * if this event is event either for the node with given path or it's
	 * child node (doesn't have to be immediate child).
	 * 
	 * @param path
	 * @return
	 * @throws RepositoryException
	 */
	boolean isAffected(String path) throws RepositoryException
	{
		return false;
	}
}