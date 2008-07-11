package brix.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * Event for new node being added.
 * 
 * @author Matej Knopp
 */
public class AddNodeEvent extends NodeEvent
{
	AddNodeEvent(Node node)
	{
		super(node);
	}

	public Node getNewNode()
	{
		return super.getNode();
	}

	@Override
	Event onNewEvent(Event event, QueueCallback queueCallback) throws RepositoryException
	{
		if (event instanceof BeforeRemoveNodeEvent)
		{
			BeforeRemoveNodeEvent e = (BeforeRemoveNodeEvent) event;
			if (getNewNode().getPath().equals(e.getNode().getPath()))
			{
				// we are removing same node as we have added. Thus it is
				// not necessary to add the remove event to queue
				queueCallback.blockAddingEvent();
				return null;
			}
		}
		return super.onNewEvent(event, queueCallback);
	}
}