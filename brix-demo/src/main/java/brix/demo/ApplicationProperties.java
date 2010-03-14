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

package brix.demo;

import java.util.Properties;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;

import brix.demo.util.FileUtils;
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
        String url = properties.getProperty("brixdemo.jcr.url");
        if (url == null || url.trim().length() == 0)
        {
            // if no url was specified generate a unique temporary one
            url = "file://" + FileUtils.getDefaultRepositoryFileName();
            properties.setProperty("brixdemo.jcr.url", url);
        }
        return url;
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

    /**
     * @return default workspace state
     */
    public String getWorkspaceDefaultState() {
        return properties.getProperty("brixdemo.jcr.defaultWorkspaceState");
    }
}
