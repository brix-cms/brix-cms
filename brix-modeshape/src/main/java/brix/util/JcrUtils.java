package brix.util;

import brix.jcr.Jcr2WorkspaceManager;
import brix.jcr.JcrSessionFactory;
import brix.workspace.WorkspaceManager;
import brix.workspace.rmi.ClientWorkspaceManager;
import org.apache.wicket.util.io.Streams;
import org.modeshape.jcr.JcrConfiguration;
import org.modeshape.jcr.JcrEngine;

import javax.jcr.Repository;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: korbinianbachl
 * Date: 16.03.11
 * Time: 14:56
 */
public class JcrUtils
{
    /**
     * Constructor
     */
    private JcrUtils()
    {

    }


    /**
     * Create a {@link brix.workspace.WorkspaceManager} implementation. If <code>url</code> starts with
     * <code>rmi://</code> an rmi based workspace manager will be created and returned. If
     * <code>url</code> is left blank, a local workspace manager will be created.
     *
     * @param url
     * @param sessionFactory
     * @return
     */
    public static WorkspaceManager createWorkspaceManager(String url,
            final JcrSessionFactory sessionFactory)
    {
        if (url == null || url.trim().length() == 0)
        {
            // create workspace manager for a file system repository
            Jcr2WorkspaceManager mgr = new Jcr2WorkspaceManager(sessionFactory);
            mgr.initialize();
            return mgr;
        }
        else if (url.startsWith("rmi://"))
        {
            // create rmi workspace manager
            return new ClientWorkspaceManager(url);
        }
        else
        {
            throw new RuntimeException("Unsupported workspace manager url: " + url);
        }
    }

    /**
     * Creates a jackrabbit repository based on the url. Accepted urls are <code>rmi://</code> and
     * <code>file://</code>
     *
     * @param url
     *            repository url
     * @throws RuntimeException
     *             if repository could not be created
     * @return repository instance
     */
    public static Repository createRepository(String url)
    {
        if (url.startsWith("file://"))
        {
            return createFileRepository(url);
        }
        else
        {
            throw new RuntimeException(
                    "Unsupported repository location url. Only prefix file:// is supported");
        }
    }

    /**
     * Creates a repository at the location specified by the url. Url must start with
     * <code>file://</code>.
     *
     * @param url
     *            repository home url
     * @throws RuntimeException
     *             if repository could not be created
     * @return repository instance
     */
    public static Repository createFileRepository(String url)
    {
        try
        {
            // ensure home dir exists
            final File home = new File(url.substring(6));
            mkdirs(home);

            // create default config file if one is not present
            File cfg = new File(home, "repository-ms.xml");
            if (!cfg.exists())
            {
                copyClassResourceToFile("/brix/demo/repository-ms.xml", cfg);
            }


            /**
             *
             *
             *
             */

            InputStream configStream = new FileInputStream(cfg);

            JcrConfiguration config = new JcrConfiguration().loadFrom(configStream);
            JcrEngine engine = config.build();
            engine.start();
            return engine.getRepository("default");


            // This was try 2 - but it won't start because of
//            ERROR - JcrEngine  - Error while verifying the engine's configuration: null
//            whatever this means
//
//            JcrConfiguration config = new JcrConfiguration();
//            config.repositorySource("BrixRepoSource")
//            .usingClass(InMemoryRepositorySource.class)
//            .setDescription("The Repository")
//            .setProperty("defaultWorkspaceName", "default");
//
//
//            JcrEngine engine = config.build();
//            engine.start();
//            return engine.getRepository("default");



//            RepositoryConfig config = RepositoryConfig.create(configStream, home.getAbsolutePath());
//            return RepositoryImpl.create(config);



//
//           --> EXAMPLE FOR REAL JCR2 INIT, REQUIRES JAVA6, THEREFORE NOT IMPLEMENTED!!!
//
//            Properties properties = new Properties();
//            properties.load(configStream);
//
//            Repository repository = null;
//
//            for (RepositoryFactory factory : ServiceLoader.load(RepositoryFactory.class)) {
//                repository = factory.getRepository(properties);
//                if(repository != null) {
//                    break;
//                }
//            }
//
//            return repository;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not create file repository at url: " + url, e);
        }
    }


    /**
     * {@link java.io.File#mkdirs()} that throws runtime exception if it fails
     *
     * @param file
     */
    public static void mkdirs(java.io.File file)
    {
        if (!file.exists())
        {
            if (!file.mkdirs())
            {
                throw new RuntimeException("Could not create directory: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * Copies a resource from classpath to a {@link java.io.File}
     *
     * @param source
     *            classpath to resource
     * @param destination
     *            destination file
     */
    public static void copyClassResourceToFile(String source, java.io.File destination)
    {
        final InputStream in = JcrUtils.class.getResourceAsStream(source);
        if (in == null)
        {
            throw new RuntimeException("Class resource: " + source + " does not exist");
        }

        try
        {
            final FileOutputStream fos = new FileOutputStream(destination);
            Streams.copy(in, fos);
            fos.close();
            in.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not copy class resource: " + source +
                " to destination: " + destination.getAbsolutePath());
        }
    }

}
