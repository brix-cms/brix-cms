package brix.jcr.api;

import javax.jcr.NodeIterator;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrNodeIterator extends NodeIterator
{

    public static class Wrapper
    {
        public static JcrNodeIterator wrap(NodeIterator delegate, JcrSession session)
        {
            return WrapperAccessor.JcrNodeIteratorWrapper.wrap(delegate, session);
        }
    };

    public NodeIterator getDelegate();

    public JcrNode nextNode();

}