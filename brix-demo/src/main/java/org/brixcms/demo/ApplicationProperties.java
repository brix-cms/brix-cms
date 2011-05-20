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

package org.brixcms.demo;

import org.brixcms.demo.util.PropertyUtils;
import org.brixcms.demo.util.PropertyUtils.MergeMode;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import java.io.File;
import java.util.Properties;

/**
 * Application-wide configuration settings for Brix Demo Application
 *
 * @author igor.vaynberg
 */
public class ApplicationProperties {
// ------------------------------ FIELDS ------------------------------

    private final Properties properties;
    private String prefix;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * @deprecated
     */
    public ApplicationProperties() {
        this("brix.demo");
    }

    public ApplicationProperties(String prefix) {
        this.prefix = prefix;
        // load base properties
        String baseProperties = "org/brixcms/demo" + "/application.properties";

        Properties base = PropertyUtils.loadFromClassPath(baseProperties, false);

        // load user-specific property overrides
        String username = System.getProperty("user.name");
        String userProperties = prefix.replace(".", "/") + "/application." + username + ".properties";
        Properties user = PropertyUtils.loadFromClassPath(userProperties, false);

        // load system properties
        Properties system = System.getProperties();

        // merge properties
        properties = PropertyUtils.merge(MergeMode.OVERRIDE_ONLY, base, user, system);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public String getPrefix() {
        return prefix;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * @return jcr {@link Credentials} built from username and password
     */
    public Credentials buildSimpleCredentials() {
        return new SimpleCredentials(getJcrLogin(), getJcrPassword().toCharArray());
    }

    /**
     * @return jcr login name
     */
    public String getJcrLogin() {
        return properties.getProperty(prefix + ".jcr.login");
    }

    /**
     * @return http port the server is using
     */
    public int getHttpPort() {
        return Integer.parseInt(properties.getProperty(prefix + ".httpPort"));
    }

    /**
     * @return https port the server is using
     */
    public int getHttpsPort() {
        return Integer.parseInt(properties.getProperty(prefix + ".httpsPort"));
    }

    /**
     * @return jcr default workspace
     */
    public String getJcrDefaultWorkspace() {
        return properties.getProperty(prefix + ".jcr.defaultWorkspace");
    }

    /**
     * @return jcr login password
     */
    public String getJcrPassword() {
        return properties.getProperty(prefix + ".jcr.password");
    }

    /**
     * @return jcr repository url
     */
    public String getJcrRepositoryUrl() {
        String url = properties.getProperty(prefix + ".jcr.url");
        if (url == null || url.trim().length() == 0) {
            // if no url was specified generate a unique temporary one
            url = "file://" + getDefaultRepositoryFileName();
            properties.setProperty(prefix + ".jcr.url", url);
        }
        return url;
    }

    /**
     * Generates a temporary file name inside tmp directory
     *
     * @return
     */
    public String getDefaultRepositoryFileName() {
        String fileName = System.getProperty("java.io.tmpdir");
        if (!fileName.endsWith(File.separator)) {
            fileName += File.separator;
        }
        fileName += prefix + ".repository";
        return fileName;
    }

    /**
     * @return default workspace state
     */
    public String getWorkspaceDefaultState() {
        return properties.getProperty(prefix + ".jcr.defaultWorkspaceState");
    }

    /**
     * @return workspace manager url
     */
    public String getWorkspaceManagerUrl() {
        return properties.getProperty(prefix + ".workspaceManagerUrl");
    }
}
