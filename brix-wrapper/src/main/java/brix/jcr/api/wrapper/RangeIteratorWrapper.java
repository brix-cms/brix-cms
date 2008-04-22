package brix.jcr.api.wrapper;

import javax.jcr.RangeIterator;

import brix.jcr.api.JcrSession;

/**
 * 
 * @author Matej Knopp
 */
class RangeIteratorWrapper extends AbstractWrapper implements RangeIterator
{

    protected RangeIteratorWrapper(RangeIterator delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static RangeIteratorWrapper wrap(RangeIterator delegate, JcrSession session)
    {
        if (delegate == null)
            return null;
        else
            return new RangeIteratorWrapper(delegate, session);
    }

    @Override
    public RangeIterator getDelegate()
    {
        return (RangeIterator)super.getDelegate();
    }

    public long getPosition()
    {
        return getDelegate().getPosition();
    }

    public long getSize()
    {
        return getDelegate().getSize();
    }

    public void skip(long skipNum)
    {
        getDelegate().skip(skipNum);
    }

    public boolean hasNext()
    {
        return getDelegate().hasNext();
    }

    public Object next()
    {
        return getDelegate().next();
    }

    public void remove()
    {
        getDelegate().remove();
    }

}
