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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;


public abstract class AbstractThreadLocalSessionFactory {
    static final Logger logger = LoggerFactory.getLogger(AbstractThreadLocalSessionFactory.class);

    ThreadLocal<Map<String, Session>> container = new ThreadLocal<Map<String, Session>>() {
        @Override
        protected Map<String, Session> initialValue() {
            return new HashMap<String, Session>();
        }
    };

    public AbstractThreadLocalSessionFactory() {
        super();
    }

    public void cleanup() {
        for (Session session : container.get().values()) {
            if (session.isLive()) {
                session.logout();
            }
        }
        container.get().clear();
    }

    public Session createSession(String workspace) throws CannotOpenJcrSessionException {
        try {
            final Credentials credentials = getCredentials();
            logger.debug("Opening unmanaged jcr session to workspace: {} with credentials: {}",
                    workspace, credentials);
            return getRepository().login(credentials, workspace);
        }
        catch (Exception e) {
            throw new CannotOpenJcrSessionException(workspace, e);
        }
    }

    public Session getCurrentSession(String workspace) {
        final Map<String, Session> map = container.get();
        Session session = map.get(workspace);
        if (session != null && !session.isLive()) {
            session = null;
        }
        if (session == null) {
            try {
                final Credentials credentials = getCredentials();
                logger.debug("Opening managed jcr session to workspace: {} with credentials: {}",
                        workspace, credentials);
                session = getRepository().login(credentials, workspace);
            }
            catch (Exception e) {
                throw new CannotOpenJcrSessionException(workspace, e);
            }
            map.put(workspace, session);
            container.set(map);
        }
        return session;
    }

    protected abstract Credentials getCredentials();

    protected abstract Repository getRepository();
}