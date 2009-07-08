package brix.jcr.api;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;

import brix.jcr.api.wrapper.WrapperAccessor;

/**
 * 
 * @author Matej Knopp
 */
public interface JcrProperty extends JcrItem, Property
{

    public static class Wrapper
    {
        public static JcrProperty wrap(Property delegate, JcrSession session)
        {
            return WrapperAccessor.JcrPropertyWrapper.wrap(delegate, session);
        }
    };

    public Property getDelegate();

    public boolean getBoolean();

    public Calendar getDate();

    public PropertyDefinition getDefinition();

    public double getDouble();

    public long getLength();

    public long[] getLengths();

    public long getLong();

    public JcrNode getNode();

    public InputStream getStream();

    public String getString();

    public int getType();

    public JcrValue getValue();

    public JcrValue[] getValues();

    public void setValue(Value value);

    public void setValue(Value[] values);

    public void setValue(String value);

    public void setValue(String[] values);

    public void setValue(InputStream value);

    public void setValue(long value);

    public void setValue(double value);

    public void setValue(Calendar value);

    public void setValue(boolean value);

    public void setValue(Node value);

}