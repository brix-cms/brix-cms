package brix.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * This event is only passed to other event's {@link #onNewEvent(Event)}
 * method. Not that this event will never be added to the queue (because the
 * Node becomes invalid after deletion). Instead {@link RemoveNodeEvent}
 * will be added to the queue.
 * 
 * @author Matej Knopp
 */
public class BeforeRemoveNodeEvent extends NodeEvent
{

	BeforeRemoveNodeEvent(Node node)
	{
		super(node);
	}

	@Override
	protected Node getNode()
	{
		return super.getNode();
	}

	@Override
	public Event transformBeforeAddingToQueue() throws RepositoryException
	{
		return new RemoveNodeEvent(getNode());
	}

}