package brix.jcr.base.wrapper;

import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

class VersionHistoryWrapper extends NodeWrapper implements VersionHistory
{

	private VersionHistoryWrapper(VersionHistory delegate, SessionWrapper session)
	{
		super(delegate, session);
	}

	@Override
	public VersionHistory getDelegate()
	{
		return (VersionHistory) super.getDelegate();
	}
	
	public static VersionHistoryWrapper wrap(VersionHistory history, SessionWrapper session)
	{
		if (history == null)
		{
			return null;
		}
		else
		{
			return new VersionHistoryWrapper(history, session);
		}
	}

	public void addVersionLabel(String versionName, String label, boolean moveLabel) throws RepositoryException
	{
		getDelegate().addVersionLabel(versionName, label, moveLabel);
	}

	public VersionIterator getAllVersions() throws RepositoryException
	{
		return VersionIteratorWrapper.wrap(getDelegate().getAllVersions(), getSessionWrapper());
	}

	public Version getRootVersion() throws RepositoryException
	{
		return VersionWrapper.wrap(getDelegate().getRootVersion(), getSessionWrapper());
	}

	public Version getVersion(String versionName) throws RepositoryException
	{
		return VersionWrapper.wrap(getDelegate().getVersion(versionName), getSessionWrapper());
	}

	public Version getVersionByLabel(String label) throws RepositoryException
	{
		return VersionWrapper.wrap(getDelegate().getVersionByLabel(label), getSessionWrapper());
	}

	public String[] getVersionLabels() throws RepositoryException
	{
		return getDelegate().getVersionLabels();
	}

	public String[] getVersionLabels(Version version) throws RepositoryException
	{
		return getDelegate().getVersionLabels(version);
	}

	public String getVersionableUUID() throws RepositoryException
	{
		return getVersionableUUID();
	}

	public boolean hasVersionLabel(String label) throws RepositoryException
	{
		return getDelegate().hasVersionLabel(label);
	}

	public boolean hasVersionLabel(Version version, String label) throws RepositoryException
	{
		return getDelegate().hasVersionLabel(unwrap(version), label);
	}

	public void removeVersion(String versionName) throws RepositoryException
	{
		getDelegate().removeVersion(versionName);
	}

	public void removeVersionLabel(String label) throws RepositoryException
	{
		getDelegate().removeVersionLabel(label);
	}
}
