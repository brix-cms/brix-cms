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

import javax.jcr.Session;

public interface JcrSessionFactory {
    /**
     * Creates a new session. Sessions returned by this method are not managed by the session factory implementation,
     * the callee is responsible for closing the session.
     *
     * @param workspace
     * @return
     * @throws CannotOpenJcrSessionException
     */
    Session createSession(String workspace) throws CannotOpenJcrSessionException;

    /**
     * Gets current session for specified workspace
     *
     * @param workspace workspace name or <code>null</code> for default
     * @return jcr session
     */
    Session getCurrentSession(String workspace) throws CannotOpenJcrSessionException;
}
