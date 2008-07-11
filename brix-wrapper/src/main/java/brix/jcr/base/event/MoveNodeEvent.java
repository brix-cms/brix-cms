package brix.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Event for node being moved.
 * 
 * @author Matej Knopp
 */
public class MoveNodeEvent extends NodeEvent
{
	private final String originalPath;

	MoveNodeEvent(Node node, String originalPath)
	{
		super(node);
		this.originalPath = originalPath;
	}

	@Override
	Event onNewEvent(Event event, QueueCallback callback) throws RepositoryException
	{
		if (event instanceof MoveNodeEvent)
		{
			MoveNodeEvent e = (MoveNodeEvent) event;
			if (e.getNode().getPath().equals(getNode().getPath()))
			{
				callback.blockAddingEvent();
				return this;
			}
		}
		return super.onNewEvent(event, callback);
	}

	@Override
	public Node getNode()
	{
		return super.getNode();
	}

	public String getOriginalPath()
	{
		return originalPath;
	}
}