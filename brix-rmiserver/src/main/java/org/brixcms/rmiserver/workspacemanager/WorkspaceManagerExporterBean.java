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

package org.brixcms.rmiserver.workspacemanager;

import org.brixcms.workspace.WorkspaceManager;
import org.brixcms.workspace.rmi.ServerWorkspaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;

public class WorkspaceManagerExporterBean implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory
            .getLogger(WorkspaceManagerExporterBean.class);

    private int registryPort;
    private String serviceName;
    private WorkspaceManager workspaceManager;

    private Registry registry;
    private ServerWorkspaceManager server;

    @Required
    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    @Required
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Required
    public void setWorkspaceManager(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }



    public void destroy() throws Exception {
        logger.info("Unregistring Workspace Manager remote repository with name: {}", serviceName);
        registry.unbind(serviceName);
        UnicastRemoteObject.unexportObject(server, true);
    }

    public void afterPropertiesSet() throws Exception {
        try {
            registry = LocateRegistry.getRegistry(registryPort);
            registry.list();
        } catch (Exception e) {
            registry = LocateRegistry.createRegistry(registryPort);
            registry.list();
        }

        logger.info("Exporting Workspace Manager under: {}/{}", serviceName, registry);
        server = new ServerWorkspaceManager(workspaceManager);
        RemoteStub stub = UnicastRemoteObject.exportObject(server);
        registry.rebind(serviceName, stub);
        logger.info("Exported Workspace Manager: {}", stub);
    }
}
