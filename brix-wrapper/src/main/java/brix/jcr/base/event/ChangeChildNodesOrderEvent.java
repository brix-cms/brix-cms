package brix.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Event for node children order having changed.
 * 
 * @author Matej Knopp
 */
public class ChangeChildNodesOrderEvent extends NodeEvent
{

	ChangeChildNodesOrderEvent(Node node)
	{
		super(node);
	}

	@Override
	Event onNewEvent(Event event, QueueCallback callback) throws RepositoryException
	{
		if (event instanceof ChangeChildNodesOrderEvent)
		{
			ChangeChildNodesOrderEvent e = (ChangeChildNodesOrderEvent) event;
			if (e.getNode().getPath().equals(getNode().getPath()))
			{
				// remove current event, will get replaced by the new one
				return null;
			}
		}
		return super.onNewEvent(event, callback);
	}

	@Override
	public Node getNode()
	{
		return super.getNode();
	}
}