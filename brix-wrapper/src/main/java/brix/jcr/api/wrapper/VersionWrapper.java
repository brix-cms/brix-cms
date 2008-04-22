package brix.jcr.api.wrapper;

import java.util.Calendar;

import javax.jcr.version.Version;

import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrVersion;
import brix.jcr.api.JcrVersionHistory;

/**
 * 
 * @author Matej Knopp
 */
class VersionWrapper extends NodeWrapper implements JcrVersion
{

    protected VersionWrapper(Version delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrVersion wrap(Version delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new VersionWrapper(delegate, session);
        }
    }

    public static JcrVersion[] wrap(Version delegate[], JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            JcrVersion res[] = new JcrVersion[delegate.length];
            for (int i = 0; i < delegate.length; ++i)
            {
                res[i] = wrap(delegate[i], session);
            }
            return res;
        }
    }

    @Override
    public Version getDelegate()
    {
        return (Version)super.getDelegate();
    }

    public JcrVersionHistory getContainingHistory()
    {
        return executeCallback(new Callback<JcrVersionHistory>()
        {
            public JcrVersionHistory execute() throws Exception
            {
                return JcrVersionHistory.Wrapper.wrap(getDelegate().getContainingHistory(),
                        getJcrSession());
            }
        });
    }

    public Calendar getCreated()
    {
        return executeCallback(new Callback<Calendar>()
        {
            public Calendar execute() throws Exception
            {
                return getDelegate().getCreated();
            }
        });
    }

    public JcrVersion[] getPredecessors()
    {
        return executeCallback(new Callback<JcrVersion[]>()
        {
            public JcrVersion[] execute() throws Exception
            {
                return JcrVersion.Wrapper.wrap(getDelegate().getPredecessors(), getJcrSession());
            }
        });
    }

    public JcrVersion[] getSuccessors()
    {
        return executeCallback(new Callback<JcrVersion[]>()
        {
            public JcrVersion[] execute() throws Exception
            {
                return JcrVersion.Wrapper.wrap(getDelegate().getSuccessors(), getJcrSession());
            }
        });
    }

}
