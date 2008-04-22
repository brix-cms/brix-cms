package brix.web.admin;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import brix.Path;

public class AppProperties
{
    private static final String FILE_PREFIX = "com/ibg/dexter/web/application.";
    private String svnRepository;
    private String svnUsername;
    private String svnPassword;

    private Path adminRoot;
    private Path siteRoot;
    private Path siteRoots;

    private String jcrRepositoryLocation;
    private String jcrLogin;
    private String jcrPassword;
    private String jcrDefaultWorkspace;

    public AppProperties() throws IOException
    {
        set(FILE_PREFIX + "properties");
        String uname = System.getProperty("user.name");
        if (uname != null)
        {
            set(FILE_PREFIX + uname + ".properties");
        }
        String profile = System.getProperty("dexter.profile");
        if (profile != null)
        {
            set(FILE_PREFIX + profile + ".properties");
        }
    }

    private void set(String path) throws IOException
    {
        URL url = getClass().getClassLoader().getResource(path);
        if (url != null)
        {
            Properties props = new Properties();
            props.load(url.openStream());
            set(props);
        }

    }

    private void set(Properties properties)
    {
        if (properties.containsKey("svn.repo"))
        {
            svnRepository = properties.getProperty("svn.repo");
        }
        if (properties.containsKey("svn.repo.username"))
        {
            svnUsername = properties.getProperty("svn.repo.username");
        }
        if (properties.containsKey("svn.repo.password"))
        {
            svnPassword = properties.getProperty("svn.repo.password");
        }
        if (properties.containsKey("admin.root"))
        {
            adminRoot = new Path(properties.getProperty("admin.root"));
        }
        if (properties.containsKey("site.root"))
        {
            siteRoot = new Path(properties.getProperty("site.root"));
        }
        if (properties.containsKey("site.roots"))
        {
            siteRoots = new Path(properties.getProperty("site.roots"));
        }
        if (properties.containsKey("jcr.repository.location"))
        {
            jcrRepositoryLocation = properties.getProperty("jcr.repository.location");
        }

        if (properties.containsKey("jcr.login"))
        {
            jcrLogin = properties.getProperty("jcr.login");
        }
        if (properties.containsKey("jcr.login"))
        {
            jcrPassword = properties.getProperty("jcr.password");
        }
        if (properties.containsKey("jcr.defaultWorkspace"))
        {
            jcrDefaultWorkspace = properties.getProperty("jcr.defaultWorkspace");
        }

    }

    public String getSvnRepository()
    {
        return svnRepository;
    }

    public String getSvnUsername()
    {
        return svnUsername;
    }

    public String getSvnPassword()
    {
        return svnPassword;
    }

    public Path getAdminRoot()
    {
        return adminRoot;
    }

    public Path getSiteRoot()
    {
        return siteRoot;
    }

    public String[] getSiteRoots()
    {
        return siteRoots.toString().split(",");
    }

    public String getJcrRepositoryLocation()
    {
        return jcrRepositoryLocation;
    }

    public String getJcrLogin()
    {
        return jcrLogin;
    }

    public String getJcrPassword()
    {
        return jcrPassword;
    }

    public String getJcrDefaultWorkspace()
    {
        return jcrDefaultWorkspace;
    }
}
