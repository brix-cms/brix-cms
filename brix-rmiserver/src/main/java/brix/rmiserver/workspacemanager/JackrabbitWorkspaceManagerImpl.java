package brix.rmiserver.workspacemanager;


import java.util.Arrays;
import java.util.List;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.WorkspaceImpl;

import brix.workspace.AbstractWorkspaceManager;
import brix.workspace.JcrException;
import brix.workspace.WorkspaceManager;

/**
 * Implementation of Jackrabbit {@link WorkspaceManager}
 * 
 * @author igor.vaynberg
 * 
 */
public class JackrabbitWorkspaceManagerImpl extends AbstractWorkspaceManager
{
    private final RepositoryImpl repository;
    private final Credentials credentials;

    /**
     * Constructor
     * 
     * @param repository
     *            repository
     * @param credentials
     *            repository credentials
     */
    public JackrabbitWorkspaceManagerImpl(RepositoryImpl repository, Credentials credentials)
    {
        this.repository = repository;
        this.credentials = credentials;
    }


    /** {@inheritDoc} */
    @Override
    protected void createWorkspace(String workspaceName)
    {
        Session session = createSession(null);
        try
        {
            WorkspaceImpl workspace = (WorkspaceImpl)session.getWorkspace();
            workspace.createWorkspace(workspaceName);
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
        finally
        {
            closeSession(session, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getAccessibleWorkspaceNames()
    {
        Session session = createSession(null);
        try
        {
            return Arrays.asList(session.getWorkspace().getAccessibleWorkspaceNames());
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
        finally
        {
            closeSession(session, false);
        }
    }

    @Override
    protected Session createSession(String workspaceName)
    {
        try
        {
            return repository.login(credentials, workspaceName);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not login into repository", e);
        }
    }

}
