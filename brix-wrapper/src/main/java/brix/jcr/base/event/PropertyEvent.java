package brix.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

/**
 * Base event for node property.
 * 
 * @author Matej Knopp
 */
abstract class PropertyEvent extends NodeEvent
{
	private final String propertyName;

	PropertyEvent(Property property) throws RepositoryException
	{
		super(property.getParent());
		this.propertyName = property.getName();
	}

	public PropertyEvent(Node node, String propertyName)
	{
		super(node);
		this.propertyName = propertyName;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	@Override
	public Node getNode()
	{
		return super.getNode();
	}
	
	@Override
	boolean isAffected(String path) throws RepositoryException
	{
		String currentPath = getNode().getPath() + "/" + getPropertyName();
		return currentPath.startsWith(path);			
	}
	
	@Override
	Event onNewEvent(Event event, QueueCallback callback) throws RepositoryException
	{
		if (event instanceof PropertyEvent)
		{
			PropertyEvent e = (PropertyEvent) event;
			if (e.getNode().getPath().equals(getNode().getPath()) && e.getPropertyName().equals(getPropertyName()))
			{
				return null;
			}
		}
		return super.onNewEvent(event, callback);
	}
}