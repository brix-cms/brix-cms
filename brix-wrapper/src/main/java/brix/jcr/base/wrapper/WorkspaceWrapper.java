package brix.jcr.base.wrapper;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.QueryManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;

import org.xml.sax.ContentHandler;

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
			throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException,
			PathNotFoundException, ItemExistsException, LockException, RepositoryException
	{
		getActionHandler().beforeWorkspaceClone(srcWorkspace, srcAbsPath, destAbsPath);
		getDelegate().clone(srcWorkspace, srcAbsPath, destAbsPath, removeExisting);
		getActionHandler().afterWorkspaceClone(srcWorkspace, srcAbsPath, destAbsPath);
	}

	public void copy(String srcAbsPath, String destAbsPath) throws ConstraintViolationException, VersionException,
			AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException
	{
		getActionHandler().beforeWorkspaceCopy(srcAbsPath, destAbsPath);
		getDelegate().copy(srcAbsPath, destAbsPath);
		getActionHandler().afterWorkspaceCopy(srcAbsPath, destAbsPath);
	}

	public void copy(String srcWorkspace, String srcAbsPath, String destAbsPath) throws NoSuchWorkspaceException,
			ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException,
			ItemExistsException, LockException, RepositoryException
	{
		getActionHandler().beforeWorkspaceCopy(srcWorkspace, srcAbsPath, destAbsPath);
		getDelegate().copy(srcWorkspace, srcAbsPath, destAbsPath);
		getActionHandler().afterWorkspaceCopy(srcWorkspace, srcAbsPath, destAbsPath);
	}

	public String[] getAccessibleWorkspaceNames() throws RepositoryException
	{
		return getDelegate().getAccessibleWorkspaceNames();
	}

	public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior) throws PathNotFoundException,
			ConstraintViolationException, VersionException, LockException, AccessDeniedException, RepositoryException
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

	public ObservationManager getObservationManager() throws UnsupportedRepositoryOperationException,
			RepositoryException
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

	public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException,
			PathNotFoundException, ItemExistsException, ConstraintViolationException, InvalidSerializedDataException,
			LockException, AccessDeniedException, RepositoryException
	{
		getActionHandler().beforeWorkspaceImportXML(parentAbsPath);
		getDelegate().importXML(parentAbsPath, in, uuidBehavior);
		getActionHandler().afterWorkspaceImportXML(parentAbsPath);
	}

	public void move(String srcAbsPath, String destAbsPath) throws ConstraintViolationException, VersionException,
			AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException
	{
		getActionHandler().beforeWorkspaceMove(srcAbsPath, destAbsPath);
		getDelegate().move(srcAbsPath, destAbsPath);
		getActionHandler().afterWorkspaceMove(srcAbsPath, destAbsPath);
	}

	public void restore(Version[] versions, boolean removeExisting) throws ItemExistsException,
			UnsupportedRepositoryOperationException, VersionException, LockException, InvalidItemStateException,
			RepositoryException
	{		
		getDelegate().restore(versions, removeExisting);
	}
}
