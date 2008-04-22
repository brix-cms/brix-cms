package brix.jcr.wrapper;

import javax.jcr.Node;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;

public class BrixResourceNode extends BrixFileNode {

	public BrixResourceNode(Node delegate, JcrSession session) 
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
	
    public static boolean canHandle(JcrNode node)
    {
        return isFileNode(node);
    }

    
}
