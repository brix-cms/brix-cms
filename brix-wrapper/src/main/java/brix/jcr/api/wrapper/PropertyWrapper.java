package brix.jcr.api.wrapper;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrValue;

/**
 * 
 * @author Matej Knopp
 */
class PropertyWrapper extends ItemWrapper implements JcrProperty
{

    protected PropertyWrapper(Property delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrProperty wrap(Property delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new PropertyWrapper(delegate, session);
        }
    }

    @Override
    public Property getDelegate()
    {
        return (Property)super.getDelegate();
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

    public PropertyDefinition getDefinition()
    {
        return executeCallback(new Callback<PropertyDefinition>()
        {
            public PropertyDefinition execute() throws Exception
            {
                return getDelegate().getDefinition();
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

    public long getLength()
    {
        return executeCallback(new Callback<Long>()
        {
            public Long execute() throws Exception
            {
                return getDelegate().getLength();
            }
        });
    }

    public long[] getLengths()
    {
        return executeCallback(new Callback<long[]>()
        {
            public long[] execute() throws Exception
            {
                return getDelegate().getLengths();
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

    public JcrNode getNode()
    {
        return executeCallback(new Callback<JcrNode>()
        {
            public JcrNode execute() throws Exception
            {
                return JcrNode.Wrapper.wrap(getDelegate().getNode(), getJcrSession());
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

    public JcrValue getValue()
    {
        return executeCallback(new Callback<JcrValue>()
        {
            public JcrValue execute() throws Exception
            {
                return JcrValue.Wrapper.wrap(getDelegate().getValue(), getJcrSession());
            }
        });
    }

    public JcrValue[] getValues()
    {
        return executeCallback(new Callback<JcrValue[]>()
        {
            public JcrValue[] execute() throws Exception
            {
                return JcrValue.Wrapper.wrap(getDelegate().getValues(), getJcrSession());
            }
        });
    }

    public void setValue(final Value value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(Value[] values)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(getValue());
            }
        });
    }

    public void setValue(final String value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final String[] values)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(values);
            }
        });
    }

    public void setValue(final InputStream value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final long value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final double value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final Calendar value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final boolean value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(value);
            }
        });
    }

    public void setValue(final Node value)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().setValue(unwrap(value));
            }
        });
    }
}
