package brix.jcr;


import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import brix.workspace.AbstractClusteredWorkspaceManager;
import brix.workspace.JcrException;
import brix.workspace.WorkspaceManager;

/**
 * Jackrabbit specific implementation of {@link WorkspaceManager}
 * 
 * @author igor.vaynberg
 * 
 */
public class JackrabbitClusteredWorkspaceManager extends AbstractClusteredWorkspaceManager
{

    private final JcrSessionFactory sf;

    /**
     * Construction
     * 
     * @param sf
     *            session factory that will be used to feed workspace manager sessions
     */
    public JackrabbitClusteredWorkspaceManager(JcrSessionFactory sf)
    {
        super();
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    protected void createWorkspace(String workspaceName)
    {
        Session session = createSession(null);
        try
        {
        	org.apache.jackrabbit.core.WorkspaceImpl workspace = (org.apache.jackrabbit.core.WorkspaceImpl) session
					.getWorkspace();
            workspace.createWorkspace(workspaceName);
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
        finally
        {
            session.logout();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getAccessibleWorkspaceIds()
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
            session.logout();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Session createSession(String workspaceName)
    {
        return sf.createSession(workspaceName);
    }

}