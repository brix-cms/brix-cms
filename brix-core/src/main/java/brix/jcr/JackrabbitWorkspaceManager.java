package brix.jcr;


import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.core.WorkspaceImpl;

import brix.workspace.AbstractWorkspaceManager;
import brix.workspace.JcrException;
import brix.workspace.WorkspaceManager;

/**
 * Jackrabbit specific implementation of {@link WorkspaceManager}
 * 
 * @author igor.vaynberg
 * 
 */
public class JackrabbitWorkspaceManager extends AbstractWorkspaceManager
{

    private final JcrSessionFactory sf;

    /**
     * Construction
     * 
     * @param sf
     *            session factory that will be used to feed workspace manager sessions
     */
    public JackrabbitWorkspaceManager(JcrSessionFactory sf)
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

    /** {@inheritDoc} */
    @Override
    protected Session createSession(String workspaceName)
    {
        return sf.createSession(workspaceName);
    }

}