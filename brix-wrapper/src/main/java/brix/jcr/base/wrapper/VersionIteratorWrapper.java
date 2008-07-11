package brix.jcr.base.wrapper;

import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

class VersionIteratorWrapper extends BaseWrapper<VersionIterator> implements VersionIterator
{

	private VersionIteratorWrapper(VersionIterator delegate, SessionWrapper session)
	{
		super(delegate, session);
	}

	public static VersionIteratorWrapper wrap(VersionIterator delegate, SessionWrapper session)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			return new VersionIteratorWrapper(delegate, session);
		}
	}

	public Version nextVersion()
	{
		return VersionWrapper.wrap(getDelegate().nextVersion(), getSessionWrapper());
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
		return VersionWrapper.wrap((Version) getDelegate().next(), getSessionWrapper());
	}

	public void remove()
	{
		getDelegate().remove();
	}

}
