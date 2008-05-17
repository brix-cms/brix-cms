package brix.rmiserver.jackrabbit;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.jcr.Repository;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.rmi.jackrabbit.JackrabbitServerAdapterFactory;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * A spring bean that creates {@link RemoteRepository} from the {@link RepositoryImpl} instance and
 * exports it using RMI.
 * 
 * @author ivaynberg
 * 
 */
public class RemoteRepositoryExporterBean implements InitializingBean, DisposableBean
{
    private static final Logger logger = LoggerFactory
        .getLogger(RemoteRepositoryExporterBean.class);

    private Repository repository;
    private String serviceName;
    private int registryPort;

    private Registry registry;
    private RemoteRepository remote;

    @Required
    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }


    @Required
    public void setRegistryPort(int registryPort)
    {
        this.registryPort = registryPort;
    }


    @Required
    public void setRepository(Repository repository)
    {
        this.repository = repository;
    }


    public void afterPropertiesSet() throws Exception
    {

        ServerAdapterFactory factory = new JackrabbitServerAdapterFactory();
        remote = factory.getRemoteRepository(repository);

        registry = LocateRegistry.getRegistry(registryPort);
        try
        {
            registry.list(); // test registry
        }
        catch (Exception e)
        {
            registry = LocateRegistry.createRegistry(registryPort);
            registry.list(); // test registry
        }

        logger.info("Registring JackRabbit remote repository with name: {} and registry: {} ",
            serviceName, registry);

        registry.rebind(serviceName, remote);

        logger.info("JackRabbit remote server registered: " + remote);
    }


    public void destroy() throws Exception
    {
        logger.info("Unregistring JackRabbit remote repository with name: {}", serviceName);
        registry.unbind(serviceName);
        UnicastRemoteObject.unexportObject(remote, false);
    }

}
