package brix.demo;

import java.util.Properties;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import brix.demo.util.PropertyUtils;
import brix.demo.util.PropertyUtils.MergeMode;

/**
 * Application-wide configuration settings for Brix Demo Application
 * 
 * @author igor.vaynberg
 * 
 */
public class ApplicationProperties
{

    private final Properties properties;

    public ApplicationProperties()
    {
        // load base properties
        String baseProperties = "brix/demo/application.properties";
        Properties base = PropertyUtils.loadFromClassPath(baseProperties, false);

        // load user-specific property overrides
        String username = System.getProperty("user.name");
        String userProperties = "brix/demo/application." + username + ".properties";
        Properties user = PropertyUtils.loadFromClassPath(userProperties, false);

        // load system properties
        Properties system = System.getProperties();

        // merge properties
        properties = PropertyUtils.merge(MergeMode.OVERRIDE_ONLY, base, user, system);
    }

    /**
     * @return jcr repository url
     */
    public String getJcrRepositoryUrl()
    {
        return properties.getProperty("brixdemo.jcr.url");
    }

    /**
     * @return workspace manager url
     */
    public String getWorkspaceManagerUrl()
    {
        return properties.getProperty("brixdemo.workspaceManagerUrl");
    }


    /**
     * @return jcr login name
     */
    public String getJcrLogin()
    {
        return properties.getProperty("brixdemo.jcr.login");
    }

    /**
     * @return jcr login password
     */
    public String getJcrPassword()
    {
        return properties.getProperty("brixdemo.jcr.password");
    }

    /**
     * @return jcr default workspace
     */

    public String getJcrDefaultWorkspace()
    {
        return properties.getProperty("brixdemo.jcr.defaultWorkspace");
    }

    /**
     * @return jcr {@link Credentials} built from username and password
     */

    public Credentials buildSimpleCredentials()
    {
        return new SimpleCredentials(getJcrLogin(), getJcrPassword().toString().toCharArray());
    }

    /**
     * @return http port the server is using
     */
    public int getHttpPort()
    {
        return Integer.parseInt(properties.getProperty("brixdemo.httpPort"));
    }

    /**
     * @return https port the server is using
     */
    public int getHttpsPort()
    {
        return Integer.parseInt(properties.getProperty("brixdemo.httpsPort"));
    }


}
