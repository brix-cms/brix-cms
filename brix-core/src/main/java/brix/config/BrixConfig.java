package brix.config;

import brix.Path;
import brix.jcr.JcrSessionFactory;
import brix.registry.ExtensionPointRegistry;
import brix.workspace.WorkspaceManager;

public class BrixConfig
{
    private final ExtensionPointRegistry registry = new ExtensionPointRegistry();

    private AdminConfig adminConfig = new AdminConfig();

    private UriMapper uriMapper = new PrefixUriMapper(Path.ROOT);

    private int httpPort = 80;
    private int httpsPort = 443;

    private final WorkspaceManager workspaceManager;
    private final JcrSessionFactory sessionFactory;

    public BrixConfig(JcrSessionFactory sessionFactory, WorkspaceManager workspaceManager)
    {
        this.sessionFactory = sessionFactory;
        this.workspaceManager = workspaceManager;
    }


    public JcrSessionFactory getSessionFactory()
    {
        return sessionFactory;
    }


    public WorkspaceManager getWorkspaceManager()
    {
        return workspaceManager;
    }


    public AdminConfig getAdminConfig()
    {
        return adminConfig;
    }

    public ExtensionPointRegistry getRegistry()
    {
        return registry;
    }

    public int getHttpPort()
    {
        return httpPort;
    }

    public void setHttpPort(int httpPort)
    {
        this.httpPort = httpPort;
    }

    public int getHttpsPort()
    {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort)
    {
        this.httpsPort = httpsPort;
    }

    public UriMapper getUriMapper()
    {
        return uriMapper;
    }

    public void setUriMapper(UriMapper uriMapper)
    {
        this.uriMapper = uriMapper;
    }


}
