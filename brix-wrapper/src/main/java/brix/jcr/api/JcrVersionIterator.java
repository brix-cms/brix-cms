package brix.jcr.api;

import javax.jcr.version.VersionIterator;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrVersionIterator extends VersionIterator
{

    public static class Wrapper
    {
        public static JcrVersionIterator wrap(VersionIterator delegate, JcrSession session)
        {
            return WrapperAccessor.JcrVersionIteratorWrapper.wrap(delegate, session);
        }
    };

    public VersionIterator getDelegate();

    public JcrVersion nextVersion();

}