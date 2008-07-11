package brix.jcr.base.event;

import java.util.List;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Object that gets notifications before and after save method has been invoked
 * on item or session.
 * 
 * @author Matej Knopp
 */
public interface EventsListener
{
	/**
	 * Invoked before the {@link Item#save()} method is called on the given item
	 * or before Session#save is called, in which case the item will be
	 * <code>null</code>.
	 * 
	 * @param item
	 * @param events
	 *            list of events concerning the item or it's children that have
	 *            occurred before the save call
	 * @throws RepositoryException
	 */
	public void handleEventsBeforeSave(Session session, Item item, List<Event> events) throws RepositoryException;

	/**
	 * Invoked right after the {@link Item#save()} method was called on the given item
	 * or after Session#save is called, in which case the item will be
	 * <code>null</code>.
	 * 
	 * @param item
	 * @param events
	 *            list of events concerning the item or it's children that have
	 *            occurred before the save call
	 * @throws RepositoryException
	 */
	public void handleEventsAfterSave(Session session, Item item, List<Event> events) throws RepositoryException;
}
