package brix.rmiserver.jackrabbit;


import java.util.Collection;

import javax.jcr.RepositoryException;
import javax.security.auth.spi.LoginModule;

import org.apache.jackrabbit.core.config.AccessManagerConfig;
import org.apache.jackrabbit.core.config.ClusterConfig;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.LoginModuleConfig;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.SearchConfig;
import org.apache.jackrabbit.core.config.SecurityConfig;
import org.apache.jackrabbit.core.config.VersioningConfig;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.data.DataStore;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.xml.sax.InputSource;

/**
 * Extended config that allows us to install a factory for login modules. All in all this is a big
 * hack because jackrabbit config is not very pluggable
 * 
 * @author igor.vaynberg
 * 
 */
public abstract class ExtendedRepositoryConfig extends RepositoryConfig
{
    private final RepositoryConfig delegate;

    public ExtendedRepositoryConfig(RepositoryConfig delegate)
    {
        super(null, null, null, null, null, null, 0, null, null, null, null, null, null);
        this.delegate = delegate;
    }

    protected abstract LoginModule newLoginModule();

    @Override
    public LoginModuleConfig getLoginModuleConfig()
    {
        return new ExtendedLoginModuleConfig(delegate.getLoginModuleConfig())
        {

            @Override
            protected LoginModule newLoginModule()
            {
                return ExtendedRepositoryConfig.this.newLoginModule();
            }

        };
    }

    @Override
    public WorkspaceConfig createWorkspaceConfig(String name, InputSource template)
            throws ConfigurationException
    {
        return delegate.createWorkspaceConfig(name, template);
    }

    @Override
    public WorkspaceConfig createWorkspaceConfig(String name, StringBuffer configContent)
            throws ConfigurationException
    {
        return delegate.createWorkspaceConfig(name, configContent);
    }


    public boolean equals(Object obj)
    {
        return delegate.equals(obj);
    }

    @Override
    public AccessManagerConfig getAccessManagerConfig()
    {
        return delegate.getAccessManagerConfig();
    }

    @Override
    public String getAppName()
    {
        return delegate.getAppName();
    }

    @Override
    public ClusterConfig getClusterConfig()
    {
        return delegate.getClusterConfig();
    }

    @Override
    public String getDefaultWorkspaceName()
    {
        return delegate.getDefaultWorkspaceName();
    }

    @Override
    public DataStore getDataStore() throws RepositoryException
    {
        return delegate.getDataStore();
    }

    @Override
    public FileSystem getFileSystem() throws RepositoryException
    {
        return delegate.getFileSystem();
    }

    @Override
    public String getHomeDir()
    {
        return delegate.getHomeDir();
    }

    @Override
    public SearchConfig getSearchConfig()
    {
        return delegate.getSearchConfig();
    }

    @Override
    public SecurityConfig getSecurityConfig()
    {
        return delegate.getSecurityConfig();
    }

    @Override
    public VersioningConfig getVersioningConfig()
    {
        return delegate.getVersioningConfig();
    }

    @Override
    public WorkspaceConfig getWorkspaceConfig(String name)
    {
        return delegate.getWorkspaceConfig(name);
    }

    @Override
    public Collection getWorkspaceConfigs()
    {
        return delegate.getWorkspaceConfigs();
    }

    @Override
    public int getWorkspaceMaxIdleTime()
    {
        return delegate.getWorkspaceMaxIdleTime();
    }

    @Override
    public String getWorkspacesConfigRootDir()
    {
        return delegate.getWorkspacesConfigRootDir();
    }

    public int hashCode()
    {
        return delegate.hashCode();
    }

    public void init() throws ConfigurationException, IllegalStateException
    {
        delegate.init();
    }

    public String toString()
    {
        return delegate.toString();
    }


}
