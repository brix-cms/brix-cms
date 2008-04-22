package brix.jcr.api;

import javax.jcr.query.RowIterator;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrRowIterator extends RowIterator
{

    public static class Wrapper
    {
        public static JcrRowIterator wrap(RowIterator delegate, JcrSession session)
        {
            return WrapperAccessor.JcrRowIteratorWrapper.wrap(delegate, session);
        };
    };

    public RowIterator getDelegate();

    public JcrRow nextRow();

}