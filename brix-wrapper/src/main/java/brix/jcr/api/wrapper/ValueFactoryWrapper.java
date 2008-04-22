package brix.jcr.api.wrapper;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;

import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrValue;
import brix.jcr.api.JcrValueFactory;

/**
 * 
 * @author Matej Knopp
 */
class ValueFactoryWrapper extends AbstractWrapper implements JcrValueFactory
{

    protected ValueFactoryWrapper(ValueFactory delegate, JcrSession session)
    {
        super(delegate, session);
    }

    @Override
    public ValueFactory getDelegate()
    {
        return (ValueFactory)super.getDelegate();
    }

    public static JcrValueFactory wrap(ValueFactory delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new ValueFactoryWrapper(delegate, session);
        }
    }

    public JcrValue createValue(String value)
    {
        return JcrValue.Wrapper.wrap(getDelegate().createValue(value), getJcrSession());
    }

    public JcrValue createValue(long value)
    {
        return JcrValue.Wrapper.wrap(getDelegate().createValue(value), getJcrSession());
    }

    public JcrValue createValue(double value)
    {
        return JcrValue.Wrapper.wrap(getDelegate().createValue(value), getJcrSession());
    }

    public JcrValue createValue(boolean value)
    {
        return JcrValue.Wrapper.wrap(getDelegate().createValue(value), getJcrSession());
    }

    public JcrValue createValue(Calendar value)
    {
        return JcrValue.Wrapper.wrap(getDelegate().createValue(value), getJcrSession());
    }

    public JcrValue createValue(InputStream value)
    {
        return JcrValue.Wrapper.wrap(getDelegate().createValue(value), getJcrSession());
    }

    public JcrValue createValue(final Node value)
    {
        return executeCallback(new Callback<JcrValue>()
        {
            public JcrValue execute() throws Exception
            {
                return JcrValue.Wrapper.wrap(getDelegate().createValue(unwrap(value)),
                        getJcrSession());
            }
        });
    }

    public JcrValue createValue(String value, int type) throws ValueFormatException
    {
        return JcrValue.Wrapper.wrap(getDelegate().createValue(value), getJcrSession());
    }

}
