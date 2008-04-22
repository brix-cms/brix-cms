package brix.jcr.api.wrapper;

import java.io.InputStream;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.version.Version;

import org.xml.sax.ContentHandler;

import brix.jcr.api.JcrQueryManager;
import brix.jcr.api.JcrSession;
import brix.jcr.api.JcrWorkspace;

/**
 * 
 * @author Matej Knopp
 */
class WorkspaceWrapper extends AbstractWrapper implements JcrWorkspace
{

    protected WorkspaceWrapper(Workspace delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static JcrWorkspace wrap(Workspace delegate, JcrSession session)
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

    @Override
    public Workspace getDelegate()
    {
        return (Workspace)super.getDelegate();
    }

    public void clone(final String srcWorkspace, final String srcAbsPath, final String destAbsPath,
            final boolean removeExisting)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().clone(srcWorkspace, srcAbsPath, destAbsPath, removeExisting);
            }
        });
    }

    public void copy(final String srcAbsPath, final String destAbsPath)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().copy(srcAbsPath, destAbsPath);
            }
        });
    }

    public void copy(final String srcWorkspace, final String srcAbsPath, final String destAbsPath)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().copy(srcWorkspace, srcAbsPath, destAbsPath);
            }
        });
    }

    public String[] getAccessibleWorkspaceNames()
    {
        return executeCallback(new Callback<String[]>()
        {
            public String[] execute() throws Exception
            {
                return getDelegate().getAccessibleWorkspaceNames();
            }
        });
    }

    public ContentHandler getImportContentHandler(final String parentAbsPath, final int uuidBehavior)
    {
        return executeCallback(new Callback<ContentHandler>()
        {
            public ContentHandler execute() throws Exception
            {
                return getDelegate().getImportContentHandler(parentAbsPath, uuidBehavior);
            }
        });
    }

    public String getName()
    {
        return executeCallback(new Callback<String>()
        {
            public String execute() throws Exception
            {
                return getDelegate().getName();
            }
        });
    }

    public NamespaceRegistry getNamespaceRegistry()
    {
        return executeCallback(new Callback<NamespaceRegistry>()
        {
            public NamespaceRegistry execute() throws Exception
            {
                return getDelegate().getNamespaceRegistry();
            }
        });
    }

    public NodeTypeManager getNodeTypeManager()
    {
        return executeCallback(new Callback<NodeTypeManager>()
        {
            public NodeTypeManager execute() throws Exception
            {
                return getDelegate().getNodeTypeManager();
            }
        });
    }

    public ObservationManager getObservationManager()
    {
        return executeCallback(new Callback<ObservationManager>()
        {
            public ObservationManager execute() throws Exception
            {
                return getDelegate().getObservationManager();
            }
        });
    }

    public JcrQueryManager getQueryManager()
    {
        return executeCallback(new Callback<JcrQueryManager>()
        {
            public JcrQueryManager execute() throws Exception
            {
                return JcrQueryManager.Wrapper.wrap(getDelegate().getQueryManager(),
                        getJcrSession());
            }
        });
    }

    public JcrSession getSession()
    {
        return getJcrSession();
    }

    public void importXML(final String parentAbsPath, final InputStream in, final int uuidBehavior)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().importXML(parentAbsPath, in, uuidBehavior);
            }
        });
    }

    public void move(final String srcAbsPath, final String destAbsPath)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().move(srcAbsPath, destAbsPath);
            }
        });
    }

    public void restore(final Version[] versions, final boolean removeExisting)
    {
        executeCallback(new VoidCallback()
        {
            public void execute() throws Exception
            {
                getDelegate().restore(versions, removeExisting);
            }
        });
    }

}
