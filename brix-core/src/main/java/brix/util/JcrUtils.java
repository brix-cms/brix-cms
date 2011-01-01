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

package brix.util;

import brix.jcr.Jcr2WorkspaceManager;
import brix.jcr.JcrSessionFactory;
import brix.workspace.WorkspaceManager;
import brix.workspace.rmi.ClientWorkspaceManager;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.io.Streams;

import javax.jcr.Repository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Jcr and Jackrabbit related utilities
 * 
 * @author igor.vaynberg
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
     * Create a {@link WorkspaceManager} implementation. If <code>url</code> starts with
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
            File cfg = new File(home, "repository.xml");
            if (!cfg.exists())
            {
                copyClassResourceToFile("/brix/demo/repository.xml", cfg);
            }

            //TODO: try to clean from Jackrabbit dependency
            InputStream configStream = new FileInputStream(cfg);
            RepositoryConfig config = RepositoryConfig.create(configStream, home.getAbsolutePath());
            return RepositoryImpl.create(config);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not create file repository at url: " + url, e);
        }
    }

    /**
     * Creates a repository at the location specified by the url. Url must start with
     * <code>rmi://</code>.
     * 
     * @param url
     *            repository home url
     * @throws RuntimeException
     *             if repository could not be created
     * @return repository instance
     */
    public static Repository createRmiRepository(String url)
    {
        try
        {
            JcrUtils.class.getClassLoader().loadClass("org.apache.jackrabbit.rmi.client.ClientRepositoryFactory");

            return RmiRepositoryFactory.getRmiRepository(url);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not create rmi repository instance at url: " + url, e);
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
