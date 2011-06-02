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

package org.brixcms.config;

import org.brixcms.jcr.JcrSessionFactory;
import org.brixcms.registry.ExtensionPointRegistry;
import org.brixcms.workspace.WorkspaceManager;

public class BrixConfig {
    private final ExtensionPointRegistry registry = new ExtensionPointRegistry();

    private AdminConfig adminConfig = new AdminConfig();

    private final UriMapper mapper;

    private int httpPort = 80;
    private int httpsPort = 443;

    private final WorkspaceManager workspaceManager;
    private final JcrSessionFactory sessionFactory;

    public BrixConfig(JcrSessionFactory sessionFactory, WorkspaceManager workspaceManager,
                      UriMapper mapper) {
        this.sessionFactory = sessionFactory;
        this.workspaceManager = workspaceManager;
        this.mapper = mapper;
    }

    public AdminConfig getAdminConfig() {
        return adminConfig;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getHttpsPort() {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    public UriMapper getMapper() {
        return mapper;
    }

    public ExtensionPointRegistry getRegistry() {
        return registry;
    }

    public JcrSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }
}
