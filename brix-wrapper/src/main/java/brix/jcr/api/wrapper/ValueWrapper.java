package brix.jcr.api.wrapper;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Value;

import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrValue;

/**
 * 
 * @author Matej Knopp
 */
class ValueWrapper extends AbstractWrapper implements JcrValue
{

    protected ValueWrapper(Value delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrValue wrap(Value delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new ValueWrapper(delegate, session);
        }
    }

    public static JcrValue[] wrap(Value[] delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            JcrValue result[] = new JcrValue[delegate.length];
            for (int i = 0; i < delegate.length; ++i)
            {
                result[i] = wrap(delegate[i], session);
            }
            return result;
        }
    }

    @Override
    public Value getDelegate()
    {
        return (Value)super.getDelegate();
    }

    public boolean getBoolean()
    {
        return executeCallback(new Callback<Boolean>()
        {
            public Boolean execute() throws Exception
            {
                return getDelegate().getBoolean();
            }
        });
    }

    public Calendar getDate()
    {
        return executeCallback(new Callback<Calendar>()
        {
            public Calendar execute() throws Exception
            {
                return getDelegate().getDate();
            }
        });
    }

    public double getDouble()
    {
        return executeCallback(new Callback<Double>()
        {
            public Double execute() throws Exception
            {
                return getDelegate().getDouble();
            }
        });
    }

    public long getLong()
    {
        return executeCallback(new Callback<Long>()
        {
            public Long execute() throws Exception
            {
                return getDelegate().getLong();
            }
        });
    }

    public InputStream getStream()
    {
        return executeCallback(new Callback<InputStream>()
        {
            public InputStream execute() throws Exception
            {
                return getDelegate().getStream();
            }
        });
    }

    public String getString()
    {
        return executeCallback(new Callback<String>()
        {
            public String execute() throws Exception
            {
                return getDelegate().getString();
            }
        });
    }

    public int getType()
    {
        return executeCallback(new Callback<Integer>()
        {
            public Integer execute() throws Exception
            {
                return getDelegate().getType();
            }
        });
    }

}
