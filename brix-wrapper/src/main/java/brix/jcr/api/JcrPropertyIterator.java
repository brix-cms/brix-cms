package brix.jcr.api;

import javax.jcr.PropertyIterator;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrPropertyIterator extends PropertyIterator
{

    public static class Wrapper
    {
        public static JcrPropertyIterator wrap(PropertyIterator delegate, JcrSession session)
        {
            return WrapperAccessor.JcrPropertyIteratorWrapper.wrap(delegate, session);
        }
    };

    public PropertyIterator getDelegate();

    public JcrProperty nextProperty();

}