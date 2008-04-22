package brix.jcr.api.wrapper;

import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import brix.jcr.api.JcrRow;
import brix.jcr.api.JcrRowIterator;
import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class RowIteratorWrapper extends RangeIteratorWrapper implements JcrRowIterator
{

    protected RowIteratorWrapper(RowIterator delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrRowIterator wrap(RowIterator delegate, JcrSession session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new RowIteratorWrapper(delegate, session);
        }
    };

    @Override
    public RowIterator getDelegate()
    {
        return (RowIterator)super.getDelegate();
    }

    public JcrRow nextRow()
    {
        return JcrRow.Wrapper.wrap(getDelegate().nextRow(), getJcrSession());
    }

    @Override
    public Object next()
    {
        return JcrRow.Wrapper.wrap((Row)getDelegate().next(), getJcrSession());
    }
}
