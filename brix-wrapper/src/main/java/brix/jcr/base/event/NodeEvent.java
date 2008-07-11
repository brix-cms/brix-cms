package brix.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;


/**
 * Abstract event for events with node.
 * 
 * @author Matej Knopp
 */
abstract class NodeEvent extends Event
{
	final Node node;

	NodeEvent(Node node)
	{
		this.node = node;
	}

	Node getNode()
	{
		return node;
	}

	@Override
	Event onNewEvent(Event event, QueueCallback queueCallback) throws RepositoryException
	{
		// if this event's node or some of it's parent is being removed this
		// event should be removed as well
		if (event instanceof BeforeRemoveNodeEvent)
		{
			BeforeRemoveNodeEvent e = (BeforeRemoveNodeEvent) event;
			if (getNode().getPath().startsWith(e.getNode().getPath()))
			{
				return null;
			}
		}
		return this;
	}

	@Override
	boolean isAffected(String path) throws RepositoryException
	{
		String currentPath = getNode().getPath();
		return currentPath.startsWith(path);
	}
}