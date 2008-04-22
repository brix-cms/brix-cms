package brix.jcr.api.wrapper;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RangeIterator;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class NodeIteratorWrapper extends RangeIteratorWrapper implements JcrNodeIterator
{

    protected NodeIteratorWrapper(RangeIterator delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrNodeIterator wrap(NodeIterator delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new NodeIteratorWrapper(delegate, session);
        }
    }

    @Override
    public NodeIterator getDelegate()
    {
        return (NodeIterator)super.getDelegate();
    }

    @Override
    public Object next()
    {
        return JcrNode.Wrapper.wrap((Node)getDelegate().next(), getJcrSession());
    }

    public JcrNode nextNode()
    {
        return JcrNode.Wrapper.wrap(getDelegate().nextNode(), getJcrSession());
    }

}
