package brix.jcr.wrapper;

import brix.jcr.NodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;

public class BrixResourceNode extends BrixFileNode
{

    public static NodeWrapperFactory FACTORY = new NodeWrapperFactory()
    {

        @Override
        public boolean canWrap(JcrNode node)
        {
            return isFileNode(node);
        }

        @Override
        public JcrNode wrap(JcrNode node)
        {
            return new BrixResourceNode(node, node.getSession());
        }
    };


    public BrixResourceNode(JcrNode delegate, JcrSession session)
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


}
