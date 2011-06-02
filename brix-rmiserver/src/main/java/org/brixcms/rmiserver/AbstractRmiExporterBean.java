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

package org.brixcms.rmiserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteStub;
import java.rmi.server.UnicastRemoteObject;

public abstract class AbstractRmiExporterBean implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRmiExporterBean.class);

    private int registryPort;
    private String serviceName;

    private Registry registry;
    private Remote server;

    @Required
    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    @Required
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }



    public void destroy() throws Exception {
        logger.info("Unregistering " + server.getClass().getName() +
                " remote repository with name: {}", serviceName);
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


        server = createServiceInstance();
        logger.info("Exporting " + server.getClass().getName() + " under: {}/{}", serviceName,
                registry);
        RemoteStub stub = UnicastRemoteObject.exportObject(server);
        registry.rebind(serviceName, stub);
        logger.info("Exported " + server.getClass().getName() + ": {}", stub);
    }

    protected abstract Remote createServiceInstance();
}
