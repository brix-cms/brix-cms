package brix.rmiserver.workspacemanager;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.WorkspaceImpl;

import brix.jcr.api.JcrSession;
import brix.jcr.exception.JcrException;
import brix.workspace.WorkspaceManagerImpl;

public class JackrabbitWorkspaceManagerImpl extends WorkspaceManagerImpl
{
    private final RepositoryImpl repository;
    private final Credentials credentials;

    public JackrabbitWorkspaceManagerImpl(RepositoryImpl repository, Credentials credentials)
    {
        this.repository = repository;
        this.credentials = credentials;
    }

    @Override
    protected void createWorkspace(String workspaceName)
    {
        WorkspaceImpl workspace = (WorkspaceImpl)getSession(null).getWorkspace().getDelegate();
        try
        {
            workspace.createWorkspace(workspaceName);
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
    }

    @Override
    protected List<String> getAccessibleWorkspaceNames()
    {
        return Arrays.asList(getSession(null).getWorkspace().getAccessibleWorkspaceNames());
    }

    @Override
    protected JcrSession getSession(String workspaceName)
    {
        try
        {
            return JcrSession.Wrapper.wrap(repository.login(credentials, workspaceName));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not log into repository", e);
        }
    }

}
