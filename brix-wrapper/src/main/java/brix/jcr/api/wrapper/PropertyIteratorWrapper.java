package brix.jcr.api.wrapper;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RangeIterator;

import brix.jcr.api.JcrProperty;
import brix.jcr.api.JcrPropertyIterator;
import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class PropertyIteratorWrapper extends RangeIteratorWrapper implements JcrPropertyIterator
{

    protected PropertyIteratorWrapper(RangeIterator delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrPropertyIterator wrap(PropertyIterator delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new PropertyIteratorWrapper(delegate, session);
        }
    }

    @Override
    public PropertyIterator getDelegate()
    {
        return (PropertyIterator)super.getDelegate();
    }

    public JcrProperty nextProperty()
    {
        return JcrProperty.Wrapper.wrap(getDelegate().nextProperty(), getJcrSession());
    }

    @Override
    public Object next()
    {
        return JcrProperty.Wrapper.wrap((Property)getDelegate().next(), getJcrSession());
    }

}
