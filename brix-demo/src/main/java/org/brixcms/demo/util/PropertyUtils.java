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

package org.brixcms.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;

public class PropertyUtils {
    /**
     * Loads properties from a classpath resource
     *
     * @param resource
     * @param throwExceptionIfNotFound
     * @return loaded properties
     */
    public static Properties loadFromClassPath(String resource, boolean throwExceptionIfNotFound) {
        URL url = PropertyUtils.class.getClassLoader().getResource(resource);
        if (url == null) {
            if (throwExceptionIfNotFound) {
                throw new IllegalStateException("Could not find classpath properties resource: " +
                        resource);
            } else {
                return new Properties();
            }
        }
        try {
            Properties props = new Properties();
            InputStream is = url.openStream();
            try {
                props.load(url.openStream());
            } finally {
                is.close();
            }
            return props;
        } catch (IOException e) {
            throw new RuntimeException("Could not read properties at classpath resource: " +
                    resource, e);
        }
    }

    /**
     * Merges multiple {@link Properties} instances into one
     *
     * @param mode    merge mode
     * @param sources properties to merge
     * @return new instance of {@link Properties} containing merged values
     */
    public static Properties merge(MergeMode mode, Properties... sources) {
        Properties props = new Properties();

        for (int i = 0; i < sources.length; i++) {
            final Properties source = sources[i];
            for (Entry<Object, Object> prop : source.entrySet()) {
                final boolean exists = props.containsKey(prop.getKey());
                boolean set = false;
                switch (mode) {
                    case MERGE:
                        set = true;
                        break;
                    case OVERRIDE_ONLY:
                        set = exists;
                        break;
                }
                if (set || i == 0) {
                    props.put(prop.getKey(), prop.getValue());
                }
            }
        }
        return props;
    }

    /**
     * Merge mode
     *
     * @author igor.vaynberg
     */
    public static enum MergeMode {
        OVERRIDE_ONLY,
        MERGE
    }
}
