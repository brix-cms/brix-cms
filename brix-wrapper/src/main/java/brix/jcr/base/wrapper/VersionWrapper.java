package brix.jcr.base.wrapper;

import java.util.Calendar;

import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;



class VersionWrapper extends NodeWrapper implements javax.jcr.version.Version
{

	private VersionWrapper(Version delegate, SessionWrapper session)
	{
		super(delegate, session);
	}
	
	@Override
	public Version getDelegate()
	{
		return (Version) super.getDelegate();
	}

	public static VersionWrapper wrap(Version delegate, SessionWrapper session)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			return new VersionWrapper(delegate, session);
		}
	}

	public static VersionWrapper[] wrap(Version delegate[], SessionWrapper session)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			VersionWrapper result[] = new VersionWrapper[delegate.length];
			for (int i = 0; i < delegate.length; ++i)
			{
				result[i] = wrap(delegate[i], session);
			}
			return result;
		}
	}

	
	public VersionHistory getContainingHistory() throws RepositoryException
	{
		return VersionHistoryWrapper.wrap(getDelegate().getContainingHistory(), getSessionWrapper());
	}

	public Calendar getCreated() throws RepositoryException
	{
		return getDelegate().getCreated();
	}

	public Version[] getPredecessors() throws RepositoryException
	{		
		return wrap(getDelegate().getPredecessors(), getSessionWrapper());
	}

	public Version[] getSuccessors() throws RepositoryException
	{
		return wrap(getDelegate().getSuccessors(), getSessionWrapper());
	}

}
