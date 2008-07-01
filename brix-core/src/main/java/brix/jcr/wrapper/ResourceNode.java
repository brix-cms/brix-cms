package brix.jcr.wrapper;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;

/**
 * Wrapper for file nodes that are not wrapped by any other wrapper.
 * 
 * @author Matej Knopp
 */
public class ResourceNode extends BrixFileNode
{

	public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
	{
		@Override
		public boolean canWrap(Brix brix, JcrNode node)
		{
			return isFileNode(node);
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session)
		{
			return new ResourceNode(node, session);
		}
	};

	public ResourceNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

	private static final String REQUIRED_PROTOCOL = "brix:requiredProtocol";

	@Override
	public Protocol getRequiredProtocol()
	{
		if (!hasProperty(REQUIRED_PROTOCOL))
		{
			return Protocol.PRESERVE_CURRENT;
		}
		else
		{
			return Protocol.valueOf(getProperty(REQUIRED_PROTOCOL).getString());
		}
	}

	public void setRequiredProtocol(Protocol protocol)
	{
		if (protocol == null)
		{
			throw new IllegalArgumentException("Argument 'protocol' may not be null.");
		}
		setProperty(REQUIRED_PROTOCOL, protocol.toString());
	}

	@Override
	public String getUserVisibleType()
	{
		return "Resource";
	}
}
