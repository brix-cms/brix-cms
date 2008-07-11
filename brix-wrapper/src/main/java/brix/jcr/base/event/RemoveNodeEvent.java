package brix.jcr.base.event;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Event indicating that a node has been removed.
 * 
 * @author Matej
 * 
 */
public class RemoveNodeEvent extends NodeEvent
{
	private final String nodeName;
	private final String nodeUUID;

	RemoveNodeEvent(Node node) throws RepositoryException
	{
		super(node.getParent());
		this.nodeName = node.getName();
		this.nodeUUID = node.isNodeType("mix:referenceable") ? node.getUUID() : null;
	}

	public Node getParentNode()
	{
		return super.getNode();
	}

	public String getNodeName()
	{
		return nodeName;
	}

	public String getNodeUUID()
	{
		return nodeUUID;
	}
}