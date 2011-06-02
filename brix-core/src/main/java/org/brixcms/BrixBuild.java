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

package org.brixcms;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Brix build properties
 *
 * @author ivaynberg
 */
public class BrixBuild {
    private static final String NUMBER = "brix-build.number";
    private static final String FILE = "META-INF/brix-build.properties";

    /**
     * singleton instance
     */
    private static BrixBuild instance;

    /**
     * build number
     */
    private String number;

    /**
     * @return instance of {@link BrixBuild} singleton
     */
    public static synchronized BrixBuild instance() {
        if (instance == null) {
            instance = new BrixBuild();
        }
        return instance;
    }

    private BrixBuild() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(FILE);
        if (is != null) {
            Properties props = new Properties();
            try {
                props.load(is);
                is.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Could not read " + FILE);
            }

            number = props.getProperty(NUMBER);
            if (number == null || NUMBER.equals(number)) {
                number = "unknown";
            }
        }
    }

    /**
     * @return build number
     */
    public String getNumber() {
        return number;
    }
}
