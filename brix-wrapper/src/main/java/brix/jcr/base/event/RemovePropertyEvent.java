package brix.jcr.base.event;

import javax.jcr.Node;

/**
 * Event for node property being removed.
 * 
 * @author Matej Knopp
 */
public class RemovePropertyEvent extends PropertyEvent
{

	RemovePropertyEvent(Node node, String propertyName)
	{
		super(node, propertyName);
	}	
}