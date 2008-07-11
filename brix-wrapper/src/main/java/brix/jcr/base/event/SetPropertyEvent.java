package brix.jcr.base.event;

import javax.jcr.Property;
import javax.jcr.RepositoryException;


/**
 * Event for node property having changed.
 * 
 * @author Matej Knopp
 */
public class SetPropertyEvent extends PropertyEvent
{
	SetPropertyEvent(Property property) throws RepositoryException
	{
		super(property);
	}

	public Property getProperty() throws RepositoryException
	{
		return getNode().getProperty(getPropertyName());
	}	
}