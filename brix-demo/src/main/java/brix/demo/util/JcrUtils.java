package brix.demo.util;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.jcr.Repository;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.rmi.client.ClientRepositoryFactory;
import org.apache.jackrabbit.rmi.jackrabbit.JackrabbitClientAdapterFactory;
import org.apache.wicket.util.file.File;

public class JcrUtils
{
    private JcrUtils()
    {

    }

    public static Repository createRepository(String url)
    {
        if (url.startsWith("rmi://"))
        {
            return createRmiRepository(url);
        }
        else if (url.startsWith("file://"))
        {
            return createFileRepository(url);
        }
        else
        {
            throw new RuntimeException(
                "Unsupported repository location url. Only prefix rmi:// and file:// are supported");
        }
    }

    public static Repository createFileRepository(String url)
    {
        try
        {
            // ensure home dir exists
            final File home = new File(url.substring(6));
            FileUtils.mkdirs(home);

            // create default config file if one is not present
            File cfg = new File(home, "repository.xml");
            if (!cfg.exists())
            {
                FileUtils.copyClassResourceToFile("brix/demo/repository.xml", cfg);
            }

            InputStream configStream = new FileInputStream(cfg);
            RepositoryConfig config = RepositoryConfig.create(configStream, home.getAbsolutePath());
            return RepositoryImpl.create(config);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not create file repository at url: " + url, e);
        }
    }

    public static Repository createRmiRepository(String url)
    {
        try
        {
            ClientRepositoryFactory factory = new ClientRepositoryFactory(
                new JackrabbitClientAdapterFactory());
            Repository repository = factory.getRepository(url);
            return repository;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not create rmi repository instance at url: " + url, e);
        }
    }

}
