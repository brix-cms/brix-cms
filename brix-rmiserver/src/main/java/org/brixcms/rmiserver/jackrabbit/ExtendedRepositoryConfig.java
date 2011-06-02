/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.rmiserver.jackrabbit;


import org.apache.jackrabbit.core.config.AccessManagerConfig;
import org.apache.jackrabbit.core.config.ClusterConfig;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.LoginModuleConfig;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.SecurityConfig;
import org.apache.jackrabbit.core.config.VersioningConfig;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.data.DataStore;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.jackrabbit.core.query.QueryHandler;
import org.apache.jackrabbit.core.query.QueryHandlerContext;
import org.apache.jackrabbit.core.util.RepositoryLockMechanism;
import org.apache.jackrabbit.core.util.db.ConnectionFactory;
import org.xml.sax.InputSource;

import javax.jcr.RepositoryException;
import javax.security.auth.spi.LoginModule;
import java.util.Collection;

/**
 * //TODO: check if we really need this hack?!
 * <p/>
 * Extended config that allows us to install a factory for login modules. All in all this is a big hack because
 * jackrabbit config is not very pluggable
 *
 * @author igor.vaynberg
 */
public abstract class ExtendedRepositoryConfig extends RepositoryConfig {
    private final RepositoryConfig delegate;

    public ExtendedRepositoryConfig(RepositoryConfig delegate) {
        super(null, null, null, null, null, null, 0, null, null, null, null, null, null, null,
                null, null);
        this.delegate = delegate;
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public int hashCode() {
        return delegate.hashCode();
    }

    public String toString() {
        return delegate.toString();
    }


    @Override
    public DataStore getDataStore() throws RepositoryException {
        return delegate.getDataStore();
    }


    @Override
    public FileSystem getFileSystem() throws RepositoryException {
        return delegate.getFileSystem();
    }


    @Override
    public QueryHandler getQueryHandler(QueryHandlerContext context) throws RepositoryException {
        return delegate.getQueryHandler(context);
    }

    @Override
    public WorkspaceConfig createWorkspaceConfig(String name, InputSource template)
            throws ConfigurationException {
        return delegate.createWorkspaceConfig(name, template);
    }

    @Override
    public WorkspaceConfig createWorkspaceConfig(String name, StringBuffer configContent)
            throws ConfigurationException {
        return delegate.createWorkspaceConfig(name, configContent);
    }

    @Override
    public AccessManagerConfig getAccessManagerConfig() {
        return delegate.getAccessManagerConfig();
    }

    @Override
    public String getAppName() {
        return delegate.getAppName();
    }

    @Override
    public ClusterConfig getClusterConfig() {
        return delegate.getClusterConfig();
    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return delegate.getConnectionFactory();
    }

    @Override
    public String getDefaultWorkspaceName() {
        return delegate.getDefaultWorkspaceName();
    }

    @Override
    public String getHomeDir() {
        return delegate.getHomeDir();
    }

    @Override
    public LoginModuleConfig getLoginModuleConfig() {
        return new ExtendedLoginModuleConfig(delegate.getLoginModuleConfig()) {
            @Override
            protected LoginModule newLoginModule() {
                return ExtendedRepositoryConfig.this.newLoginModule();
            }
        };
    }

    protected abstract LoginModule newLoginModule();

    @Override
    public RepositoryLockMechanism getRepositoryLockMechanism() throws RepositoryException {
        return delegate.getRepositoryLockMechanism();
    }

    @Override
    public SecurityConfig getSecurityConfig() {
        return delegate.getSecurityConfig();
    }

    @Override
    public VersioningConfig getVersioningConfig() {
        return delegate.getVersioningConfig();
    }

    @Override
    public WorkspaceConfig getWorkspaceConfig(String name) {
        return delegate.getWorkspaceConfig(name);
    }

    @Override
    public Collection getWorkspaceConfigs() {
        return delegate.getWorkspaceConfigs();
    }

    @Override
    public int getWorkspaceMaxIdleTime() {
        return delegate.getWorkspaceMaxIdleTime();
    }

    @Override
    public String getWorkspacesConfigRootDir() {
        return delegate.getWorkspacesConfigRootDir();
    }

    public void init() throws ConfigurationException, IllegalStateException {
        delegate.init();
    }
}
