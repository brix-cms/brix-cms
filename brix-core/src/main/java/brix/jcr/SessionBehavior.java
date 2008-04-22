package brix.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import brix.exception.BrixException;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrSession.Behavior;
import brix.jcr.api.wrapper.NodeWrapper;
import brix.jcr.event.EventUtil;
import brix.jcr.exception.JcrException;
import brix.jcr.wrapper.WrapperRegistry;

public class SessionBehavior implements Behavior
{
    WrapperRegistry wrapperRegistry;

    public SessionBehavior()
    {
    }

    public SessionBehavior(WrapperRegistry registry)
    {
        // TODO check not null
        wrapperRegistry = registry;
    }

    public void setWrapperRegistry(WrapperRegistry wrapperRegistry)
    {
        this.wrapperRegistry = wrapperRegistry;
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
        return wrapperRegistry.wrap(n);
    }
}
