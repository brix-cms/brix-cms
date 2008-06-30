package brix.jcr;


import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;

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
public abstract class AbstractJackrabbitWorkspaceManager extends AbstractWorkspaceManager
{


    /** {@inheritDoc} */
    @Override
    protected void createWorkspace(String workspaceName)
    {
        WorkspaceImpl workspace = (WorkspaceImpl)getSession(null).getWorkspace();
        try
        {
            workspace.createWorkspace(workspaceName);
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getAccessibleWorkspaceNames()
    {
        try
        {
            return Arrays.asList(getSession(null).getWorkspace().getAccessibleWorkspaceNames());
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
    }

}