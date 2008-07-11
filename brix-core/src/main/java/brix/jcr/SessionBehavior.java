package brix.jcr;

import java.util.Collection;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import brix.Brix;
import brix.exception.BrixException;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrSession.Behavior;
import brix.jcr.api.wrapper.NodeWrapper;
import brix.jcr.base.EventUtil;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.ResourceNode;

public class SessionBehavior implements Behavior
{
    private final Brix brix;

    public SessionBehavior(Brix brix)
    {
        this.brix = brix;
    }

    public void handleException(Exception e)
    {
        if (e instanceof RepositoryException)
        {
            throw new JcrException((RepositoryException)e);
        }
        else
        {
            throw new BrixException(e);
        }
    }

    public void nodeSaved(JcrNode node)
    {
        EventUtil.raiseSaveEvent(node);
    }

    public JcrNode wrap(Node node, JcrSession session)
    {
    	if (node instanceof JcrNode)
    	{
    		return (JcrNode) node;
    	}
    	
        JcrNode n = new NodeWrapper(node, session);

        Collection<JcrNodeWrapperFactory> factories = brix.getConfig().getRegistry().lookupCollection(
            JcrNodeWrapperFactory.POINT);

        for (JcrNodeWrapperFactory factory : factories)
        {
            if (factory.canWrap(brix, n))
            {
                return factory.wrap(brix, node, session);
            }
        }

        if (ResourceNode.FACTORY.canWrap(brix, n))
        {
            return ResourceNode.FACTORY.wrap(brix, node, session);
        }

        return new BrixNode(node, session);
    }
}
