package brix.demo;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationProperties
{
    private static final Logger logger = LoggerFactory.getLogger(ApplicationProperties.class);
    private static final String FILE_PREFIX = "brix/demo/application.";

    private String jcrRepositoryLocation;
    private String jcrLogin;
    private String jcrPassword;
    private String jcrDefaultWorkspace;

    public ApplicationProperties()
    {
        try
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
        catch (IOException e)
        {
            throw new RuntimeException("Could not load application properties", e);
        }

    }

    private void set(String path) throws IOException
    {

        URL url = getClass().getClassLoader().getResource(path);
        if (url != null)
        {
            logger.info("Loading application properties: " + path);
            Properties props = new Properties();
            props.load(url.openStream());
            set(props);
        }

    }

    private void set(Properties properties)
    {
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

    public Credentials buildSimpleCredentials()
    {
        return new SimpleCredentials(getJcrLogin(), getJcrPassword().toString().toCharArray());

    }
}
