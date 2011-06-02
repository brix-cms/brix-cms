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

package org.brixcms.jcr;

import org.brixcms.Brix;
import org.brixcms.registry.ExtensionPoint;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * A repository initializer. Each initializer runs once upon system startup.
 *
 * @author igor.vaynberg
 */
public interface RepositoryInitializer {
    /**
     * Extension point used to register repository initializers
     */
    public static final ExtensionPoint<RepositoryInitializer> POINT = new ExtensionPoint<RepositoryInitializer>() {
        public Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        public String getUuid() {
            return RepositoryInitializer.class.getName();
        }
    };

    /**
     * Performs repository initialization.
     *
     * @param session session into the default repository workspace
     */
    public void initializeRepository(Brix brix, Session session) throws RepositoryException;
}
