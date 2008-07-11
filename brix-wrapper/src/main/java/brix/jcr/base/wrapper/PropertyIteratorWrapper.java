package brix.jcr.base.wrapper;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;

class PropertyIteratorWrapper extends BaseWrapper<PropertyIterator> implements PropertyIterator
{

	private PropertyIteratorWrapper(PropertyIterator delegate, SessionWrapper session)
	{
		super(delegate, session);
	}

	public static PropertyIteratorWrapper wrap(PropertyIterator delegate, SessionWrapper session)
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

	public Property nextProperty()
	{
		return PropertyWrapper.wrap(getDelegate().nextProperty(), getSessionWrapper());
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
		return PropertyWrapper.wrap((Property) getDelegate().next(), getSessionWrapper());
	}

	public void remove()
	{
		getDelegate().remove();		
	}

}
