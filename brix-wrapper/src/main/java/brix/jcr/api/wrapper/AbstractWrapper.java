package brix.jcr.api.wrapper;

import javax.jcr.RepositoryException;

import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrSession.Behavior;
import brix.jcr.exception.JcrException;

/**
 * 
 * @author Matej Knopp
 */
abstract class AbstractWrapper
{

    private final Object delegate;
    private final JcrSession session;

    protected AbstractWrapper(Object delegate, JcrSession session)
    {
        if (delegate == null)
        {
            throw new IllegalArgumentException("Argument 'delegate' may not be null.");
        }
        this.delegate = delegate;
        this.session = session;
    }

    public Object getDelegate()
    {
        return delegate;
    }

    protected JcrSession getJcrSession()
    {
        return session;
    }

    protected interface Callback<T>
    {
        public T execute() throws Exception;
    };

    protected interface VoidCallback
    {
        public void execute() throws Exception;
    };

    protected <T> T executeCallback(Callback<T> callback)
    {
        if (callback == null)
        {
            throw new IllegalArgumentException("Argument 'callback' may not be null.");
        }
        try
        {
            return callback.execute();
        }
        catch (Exception e)
        {
            handleException(e);
        }
        return null;
    }

    protected void executeCallback(VoidCallback callback)
    {
        if (callback == null)
        {
            throw new IllegalArgumentException("Argument 'callback' may not be null.");
        }
        try
        {
            callback.execute();
        }
        catch (Exception e)
        {
            handleException(e);
        }
    }

    protected void handleException(Exception e)
    {
        // TODO: This is definitely not what we want to do.
        // inspect the exception and register flash messages for certain
        // exceptions (versioning, locking, ...)

        Behavior behavior = getJcrSession().getBehavior();

        if (behavior != null)
        {
            behavior.handleException(e);
        }
        else
        {
            if (e instanceof RepositoryException)
            {
                throw new JcrException((RepositoryException)e);
            }
            else
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof AbstractWrapper == false)
        {
            return false;
        }
        AbstractWrapper that = (AbstractWrapper)obj;

        return delegate.equals(that.delegate);
    }

    @SuppressWarnings("unchecked")
    protected <T> T unwrap(T wrapper)
    {
        while (wrapper instanceof AbstractWrapper)
        {
            wrapper = (T)((AbstractWrapper)wrapper).getDelegate();
        }
        return wrapper;
    }

    public <T> T[] unwrap(T original[], T newArray[])
    {
        for (int i = 0; i < original.length; ++i)
        {
            newArray[i] = unwrap(original[i]);
        }
        return newArray;
    }

    @Override
    public int hashCode()
    {
        return delegate.hashCode();
    }

}
