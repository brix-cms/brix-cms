package brix.jcr.api;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrValueFactory extends ValueFactory
{

    public static class Wrapper
    {
        public static JcrValueFactory wrap(ValueFactory delegate, JcrSession session)
        {
            return WrapperAccessor.JcrValueFactoryWrapper.wrap(delegate, session);
        }
    };

    public ValueFactory getDelegate();

    public JcrValue createValue(String value);

    public JcrValue createValue(long value);

    public JcrValue createValue(double value);

    public JcrValue createValue(boolean value);

    public JcrValue createValue(Calendar value);

    public JcrValue createValue(InputStream value);

    public JcrValue createValue(Node value);

    public JcrValue createValue(String value, int type) throws ValueFormatException;

}