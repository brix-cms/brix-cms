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

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import javax.jcr.Repository;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * A spring bean that creates {@link RemoteRepository} from the {@link RepositoryImpl} instance and exports it using
 * RMI.
 *
 * @author ivaynberg
 */
public class RemoteRepositoryExporterBean implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory
            .getLogger(RemoteRepositoryExporterBean.class);

    private Repository repository;
    private String serviceName;
    private int registryPort;

    private Registry registry;
    private RemoteRepository remote;

    @Required
    public void setRegistryPort(int registryPort) {
        this.registryPort = registryPort;
    }

    @Required
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Required
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }



    public void destroy() throws Exception {
        logger.info("Unregistering JackRabbit remote repository with name: {}", serviceName);
        registry.unbind(serviceName);
        UnicastRemoteObject.unexportObject(remote, false);
    }

    public void afterPropertiesSet() throws Exception {
        ServerAdapterFactory factory = new ServerAdapterFactory();
        remote = factory.getRemoteRepository(repository);

        registry = LocateRegistry.getRegistry(registryPort);
        try {
            registry.list(); // test registry
        } catch (Exception e) {
            registry = LocateRegistry.createRegistry(registryPort);
            registry.list(); // test registry
        }

        logger.info("Registring JackRabbit remote repository with name: {} and registry: {} ",
                serviceName, registry);

        registry.rebind(serviceName, remote);

        logger.info("JackRabbit remote server registered: " + remote);
    }
}
