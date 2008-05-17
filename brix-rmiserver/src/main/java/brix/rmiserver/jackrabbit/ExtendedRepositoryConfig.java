package brix.rmiserver.jackrabbit;


import java.util.Collection;

import javax.security.auth.spi.LoginModule;

import org.apache.jackrabbit.core.config.AccessManagerConfig;
import org.apache.jackrabbit.core.config.ClusterConfig;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.DataStoreConfig;
import org.apache.jackrabbit.core.config.FileSystemConfig;
import org.apache.jackrabbit.core.config.LoginModuleConfig;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.SearchConfig;
import org.apache.jackrabbit.core.config.SecurityConfig;
import org.apache.jackrabbit.core.config.VersioningConfig;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
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

    public WorkspaceConfig createWorkspaceConfig(String name, InputSource template)
            throws ConfigurationException
    {
        return delegate.createWorkspaceConfig(name, template);
    }

    public WorkspaceConfig createWorkspaceConfig(String name) throws ConfigurationException
    {
        return delegate.createWorkspaceConfig(name);
    }

    public boolean equals(Object obj)
    {
        return delegate.equals(obj);
    }

    public AccessManagerConfig getAccessManagerConfig()
    {
        return delegate.getAccessManagerConfig();
    }

    public String getAppName()
    {
        return delegate.getAppName();
    }

    public ClusterConfig getClusterConfig()
    {
        return delegate.getClusterConfig();
    }

    public DataStoreConfig getDataStoreConfig()
    {
        return delegate.getDataStoreConfig();
    }

    public String getDefaultWorkspaceName()
    {
        return delegate.getDefaultWorkspaceName();
    }

    public FileSystemConfig getFileSystemConfig()
    {
        return delegate.getFileSystemConfig();
    }

    public String getHomeDir()
    {
        return delegate.getHomeDir();
    }

    public SearchConfig getSearchConfig()
    {
        return delegate.getSearchConfig();
    }

    public SecurityConfig getSecurityConfig()
    {
        return delegate.getSecurityConfig();
    }

    public VersioningConfig getVersioningConfig()
    {
        return delegate.getVersioningConfig();
    }

    public WorkspaceConfig getWorkspaceConfig(String name)
    {
        return delegate.getWorkspaceConfig(name);
    }

    public Collection getWorkspaceConfigs()
    {
        return delegate.getWorkspaceConfigs();
    }

    public int getWorkspaceMaxIdleTime()
    {
        return delegate.getWorkspaceMaxIdleTime();
    }

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
