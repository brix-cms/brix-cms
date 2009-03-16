package brix.jcr.base.wrapper;

import org.xml.sax.ContentHandler;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.QueryManager;
import javax.jcr.version.Version;
import java.io.IOException;
import java.io.InputStream;

class WorkspaceWrapper extends BaseWrapper<Workspace> implements Workspace
{

	private WorkspaceWrapper(Workspace delegate, SessionWrapper session)
	{
		super(delegate, session);
	}
	
	public static WorkspaceWrapper wrap(Workspace delegate, SessionWrapper session)
	{
		if (delegate == null)
		{
			return null;
		}
		else
		{
			return new WorkspaceWrapper(delegate, session);
		}
	}

	public void clone(String srcWorkspace, String srcAbsPath, String destAbsPath, boolean removeExisting)
			throws RepositoryException
	{
		getActionHandler().beforeWorkspaceClone(srcWorkspace, srcAbsPath, destAbsPath);
		getDelegate().clone(srcWorkspace, srcAbsPath, destAbsPath, removeExisting);
		getActionHandler().afterWorkspaceClone(srcWorkspace, srcAbsPath, destAbsPath);
	}

	public void copy(String srcAbsPath, String destAbsPath) throws RepositoryException
	{
		getActionHandler().beforeWorkspaceCopy(srcAbsPath, destAbsPath);
		getDelegate().copy(srcAbsPath, destAbsPath);
		getActionHandler().afterWorkspaceCopy(srcAbsPath, destAbsPath);
	}

	public void copy(String srcWorkspace, String srcAbsPath, String destAbsPath) throws RepositoryException
	{
		getActionHandler().beforeWorkspaceCopy(srcWorkspace, srcAbsPath, destAbsPath);
		getDelegate().copy(srcWorkspace, srcAbsPath, destAbsPath);
		getActionHandler().afterWorkspaceCopy(srcWorkspace, srcAbsPath, destAbsPath);
	}

	public String[] getAccessibleWorkspaceNames() throws RepositoryException
	{
		return getDelegate().getAccessibleWorkspaceNames();
	}

	public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior) throws RepositoryException
	{
		return getDelegate().getImportContentHandler(parentAbsPath, uuidBehavior);
	}

	public String getName()
	{
		return getDelegate().getName();
	}

	public NamespaceRegistry getNamespaceRegistry() throws RepositoryException
	{
		return getDelegate().getNamespaceRegistry();
	}

	public NodeTypeManager getNodeTypeManager() throws RepositoryException
	{
		return getDelegate().getNodeTypeManager();
	}

	public ObservationManager getObservationManager() throws RepositoryException
	{
		return getDelegate().getObservationManager();
	}

	public QueryManager getQueryManager() throws RepositoryException
	{
		return QueryManagerWrapper.wrap(getDelegate().getQueryManager(), getSessionWrapper());
	}

	public Session getSession()
	{
		return getSessionWrapper();
	}

	public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException, RepositoryException
	{
		getActionHandler().beforeWorkspaceImportXML(parentAbsPath);
		getDelegate().importXML(parentAbsPath, in, uuidBehavior);
		getActionHandler().afterWorkspaceImportXML(parentAbsPath);
	}

	public void move(String srcAbsPath, String destAbsPath) throws RepositoryException
	{
		getActionHandler().beforeWorkspaceMove(srcAbsPath, destAbsPath);
		getDelegate().move(srcAbsPath, destAbsPath);
		getActionHandler().afterWorkspaceMove(srcAbsPath, destAbsPath);
	}

	public void restore(Version[] versions, boolean removeExisting) throws RepositoryException
	{		
		getDelegate().restore(versions, removeExisting);
	}
}
