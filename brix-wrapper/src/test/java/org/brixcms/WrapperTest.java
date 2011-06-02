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

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class WrapperTest {
    private static final Logger logger = LoggerFactory.getLogger(WrapperTest.class);

    private Repository repo;
    private List<JcrSession> sessions;

    private File home;

    @After
    public void cleanup() {
        for (JcrSession session : sessions) {
            if (session.isLive()) {
                session.logout();
            }
        }
        ((JackrabbitRepository) repo).shutdown();

        delete(home);
    }

    @Before
    public void setupManager() throws IOException, RepositoryException {
        String temp = System.getProperty("java.io.tmpdir");
        home = new File(temp, getClass().getName());
        delete(home);
        home.deleteOnExit();

        if (!home.mkdirs()) {
            throw new RuntimeException("Could not create directory: " + home.getAbsolutePath());
        }

        InputStream configStream = getClass().getResourceAsStream("repository.xml");
        RepositoryConfig config = RepositoryConfig.create(configStream, home.getAbsolutePath());
        repo = RepositoryImpl.create(config);

        logger.info("Initializer Jackrabbit Repository in: " + home.getAbsolutePath());

        sessions = new ArrayList<JcrSession>();
    }

    private static void delete(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                delete(child);
            }
        }
        if (!file.delete()) {
            throw new RuntimeException("Could not delete file: " + file.getAbsolutePath());
        }
    }

    @Test
    public void testgetNodeByIdentifier() throws RepositoryException {
        JcrSession session = login();

        JcrNode node = session.getRootNode().addNode("node");
        node.addMixin(JcrConstants.MIX_REFERENCEABLE);

        assertNotNull(node.getIdentifier());

        JcrNode node1 = session.getNodeByIdentifier(node.getIdentifier());
        assertNotNull(node1);
        node1.setProperty("property", "value");
    }

    private JcrSession login() throws RepositoryException {
        Credentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
        JcrSession session = JcrSession.Wrapper.wrap(repo.login(credentials));
        sessions.add(session);
        return session;
    }

    @Test
    public void testgetNodeByUUID() throws RepositoryException {
        JcrSession session = login();

        JcrNode node = session.getRootNode().addNode("node");
        node.addMixin(JcrConstants.MIX_REFERENCEABLE);

        assertNotNull(node.getIdentifier());

        JcrNode node1 = session.getNodeByIdentifier(node.getIdentifier());
        assertNotNull(node1);
        node1.setProperty("property", "value");
    }
}
