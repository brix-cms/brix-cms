package brix.jcr.api;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Value;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrValue extends Value
{

    public static class Wrapper
    {
        public static JcrValue wrap(Value delegate, JcrSession session)
        {
            return WrapperAccessor.JcrValueWrapper.wrap(delegate, session);
        }

        public static JcrValue[] wrap(Value[] delegate, JcrSession session)
        {
            return WrapperAccessor.JcrValueWrapper.wrap(delegate, session);
        }
    };

    public Value getDelegate();

    public boolean getBoolean();

    public Calendar getDate();

    public double getDouble();

    public long getLong();

    public InputStream getStream();

    public String getString();

    public int getType();

}