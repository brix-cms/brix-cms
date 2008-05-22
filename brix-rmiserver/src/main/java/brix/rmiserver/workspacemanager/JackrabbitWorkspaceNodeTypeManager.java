package brix.rmiserver.workspacemanager;

import java.io.InputStream;

import javax.jcr.Credentials;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeManager;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;

import brix.workspace.WorkspaceNodeTypeManager;

public class JackrabbitWorkspaceNodeTypeManager implements WorkspaceNodeTypeManager
{
    private final RepositoryImpl repository;
    private final Credentials credentials;


    public JackrabbitWorkspaceNodeTypeManager(RepositoryImpl repository, Credentials credentials)
    {
        this.credentials = credentials;
        this.repository = repository;
    }

    public void registerNodeTypes(String workspace, InputStream in, String contentType,
            boolean reregisterExisting)
    {
        try
        {
            Session session = repository.login(credentials, workspace);
            Workspace ws = session.getWorkspace();
            NodeTypeManager manager = ws.getNodeTypeManager();
            NodeTypeManagerImpl impl = (NodeTypeManagerImpl)manager;
            impl.registerNodeTypes(in, contentType, reregisterExisting);
            session.save();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not register node type", e);
        }
    }

}
