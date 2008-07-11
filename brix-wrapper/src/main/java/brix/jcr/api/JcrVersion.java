package brix.jcr.api;

import java.util.Calendar;

import javax.jcr.version.Version;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrVersion extends Version, JcrNode
{

    public static class Wrapper
    {
        public static JcrVersion wrap(Version delegate, JcrSession session)
        {
            return WrapperAccessor.JcrVersionWrapper.wrap(delegate, session);
        }

        public static JcrVersion[] wrap(Version delegate[], JcrSession session)
        {
            return WrapperAccessor.JcrVersionWrapper.wrap(delegate, session);
        }
    };

    public Version getDelegate();

    public JcrVersionHistory getContainingHistory();

    public Calendar getCreated();

    public JcrVersion[] getPredecessors();

    public JcrVersion[] getSuccessors();

}