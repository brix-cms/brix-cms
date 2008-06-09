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
import brix.jcr.event.EventUtil;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.BrixResourceNode;

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
        JcrNode n = new NodeWrapper(node, session);

        Collection<NodeWrapperFactory> factories = brix.getConfig().getRegistry().lookupCollection(
            NodeWrapperFactory.POINT);

        for (NodeWrapperFactory factory : factories)
        {
            if (factory.canWrap(n))
            {
                return factory.wrap(n);
            }
        }

        if (BrixResourceNode.FACTORY.canWrap(n))
        {
            return BrixResourceNode.FACTORY.wrap(n);
        }

        return new BrixNode(n, session);
    }
}
