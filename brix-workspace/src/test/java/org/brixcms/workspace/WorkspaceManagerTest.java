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

package org.brixcms.workspace;

import org.apache.jackrabbit.api.JackrabbitRepository;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class WorkspaceManagerTest {
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceManagerTest.class);

    private Repository repo;
    private WorkspaceManager manager;

    private File home;

    @After
    public void destroyManager() {
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

        // register "brix" namespace
        Credentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
        Session session = repo.login(credentials);
        NamespaceRegistry nr = session.getWorkspace().getNamespaceRegistry();
        nr.registerNamespace("brix", "http://brix-cms.googlecode.com");
        session.save();

        manager = new LocalWorkspaceManager(repo).initialize();
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
    public void testAttributes() throws RepositoryException {
        // set up test workspaces
        final String w1name;
        final String w2name;
        {
            Workspace w1 = manager.createWorkspace();
            w1name = w1.getId();
            w1.setAttribute("color", "red");

            Workspace w2 = manager.createWorkspace();
            w2name = w2.getId();
            w2.setAttribute("color", "green");
            w2.setAttribute("background", "transparent");
        }

        // test workspace 1
        {
            Workspace w = manager.getWorkspace(w1name);

            Iterator<String> keys = w.getAttributeKeys();
            assertTrue(keys.hasNext());
            assertEquals("color", keys.next());
            assertFalse(keys.hasNext());

            assertEquals("red", w.getAttribute("color"));
            assertNull(w.getAttribute("background"));
        }

        // test workspace 2
        {
            Workspace w = manager.getWorkspace(w2name);

            Iterator<String> it = w.getAttributeKeys();
            Set<String> keys = new HashSet<String>();
            while (it.hasNext()) {
                keys.add(it.next());
            }
            Collection<String> expected = Arrays.asList(new String[]{"color", "background"});
            assertTrue(keys.containsAll(expected));
            assertTrue(expected.containsAll(keys));

            assertEquals("green", w.getAttribute("color"));
            assertEquals("transparent", w.getAttribute("background"));
        }
    }

    @Test
    public void testInitialization() throws RepositoryException {
        // set up test workspaces
        Workspace w1 = manager.createWorkspace();
        w1.setAttribute("color", "green");
        w1.setAttribute("foo", "bar");

        Workspace w2 = manager.createWorkspace();
        w2.setAttribute("color", "green");
        w2.setAttribute("foo", "baz");

        Workspace w3 = manager.createWorkspace();
        w3.setAttribute("color", "green");
        w3.setAttribute("foo", "baz");

        w2.delete();

        WorkspaceManager local = new LocalWorkspaceManager(repo).initialize();

        // test workspace retrieval
        List<Workspace> result = local.getWorkspaces();
        assertEquals(2, result.size());
        assertTrue(result.contains(w1));
        assertTrue(result.contains(w3));

        // test attribute retrieval against deleted workspaces
        HashMap<String, String> attrs = new HashMap<String, String>();
        attrs.put("color", "green");
        attrs.put("foo", "baz");
        result = local.getWorkspacesFiltered(attrs);
        assertEquals(1, result.size());
        assertTrue(result.contains(w3));

        // test attribute retrieval against existing workspaces
        attrs.clear();
        attrs.put("color", "green");
        result = local.getWorkspacesFiltered(attrs);
        assertEquals(2, result.size());
        assertTrue(result.contains(w1));
        assertTrue(result.contains(w3));
    }

    @Test
    public void testWorkspaceCreation() throws RepositoryException {
        assertEquals(0, manager.getWorkspaces().size());

        Workspace w1 = manager.createWorkspace();
        assertNotNull(w1);
        assertEquals(1, manager.getWorkspaces().size());

        Workspace w2 = manager.createWorkspace();
        assertNotNull(w2);
        assertEquals(2, manager.getWorkspaces().size());

        // test retrieval of created workspaces
        Workspace w11 = manager.getWorkspace(w1.getId());
        assertNotNull(w11);
        assertEquals(w1.getId(), w11.getId());
    }

    @Test
    public void testWorkspaceDeletion() throws RepositoryException {
        assertEquals(0, manager.getWorkspaces().size());

        Workspace w1 = manager.createWorkspace();
        Workspace w2 = manager.createWorkspace();
        assertEquals(2, manager.getWorkspaces().size());

        w1.delete();

        // assert w1 is not listed
        assertEquals(1, manager.getWorkspaces().size());
        assertEquals(w2.getId(), manager.getWorkspaces().get(0).getId());

        // assert w1 can no longer be retrieved
        assertNull(manager.getWorkspace(w1.getId()));

        w2.delete();

        // assert w2 is not listed
        assertEquals(0, manager.getWorkspaces().size());

        // assert w2 cannot be retrieved
        assertNull(manager.getWorkspace(w2.getId()));
    }

    @Test
    public void testWorkspaceFiltering() throws RepositoryException {
        // set up test workspaces
        Workspace w1 = manager.createWorkspace();
        w1.setAttribute("color", "green");
        w1.setAttribute("foo", "bar");

        Workspace w2 = manager.createWorkspace();
        w2.setAttribute("color", "green");
        w2.setAttribute("background", "transparent");
        w2.setAttribute("foo", "baz");

        // test non-existent match
        HashMap<String, String> attrs = new HashMap<String, String>();
        attrs.put("color", "green");
        attrs.put("foo", "boo"); // no workspace has this attr
        assertEquals(0, manager.getWorkspacesFiltered(attrs).size());

        // test single match
        attrs.clear();
        attrs.put("color", "green");
        attrs.put("foo", "baz");
        List<Workspace> result = manager.getWorkspacesFiltered(attrs);
        assertEquals(1, result.size());
        assertEquals(w2.getId(), result.get(0).getId());

        // test multiple match
        attrs.clear();
        attrs.put("color", "green");
        result = manager.getWorkspacesFiltered(attrs);
        assertEquals(2, result.size());
    }

    @Test
    public void testWorkspaceIdLength() {
        // some database systems do not allow table names over 30 characters
        // (oracle), make sure we do not cross that limit
        for (int i = 0; i < 10; i++) {
            Workspace w = manager.createWorkspace();
            assertNotNull(w);
            System.out.println(w.getId() + " " + w.getId().length());
            assertTrue(30 >= w.getId().length());
        }
    }

    private static class LocalWorkspaceManager extends AbstractSimpleWorkspaceManager {
        private final Repository repo;

        private LocalWorkspaceManager(Repository repo) {
            this.repo = repo;
        }

        public Repository getRepo() {
            return repo;
        }

        @Override
        protected Session createSession(String workspaceName) {
            Credentials credentials = new SimpleCredentials("admin", "admin".toCharArray());
            try {
                return repo.login(credentials, workspaceName);
            } catch (RepositoryException e) {
                throw new JcrException(e);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void createWorkspace(String workspaceName) {
            Session session = createSession(null);
            try {
                org.apache.jackrabbit.core.WorkspaceImpl workspace = (org.apache.jackrabbit.core.WorkspaceImpl) session
                        .getWorkspace();
                workspace.createWorkspace(workspaceName);
            } catch (RepositoryException e) {
                throw new JcrException(e);
            } finally {
                closeSession(session, false);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<String> getAccessibleWorkspaceNames() {
            Session session = createSession(null);
            try {
                return Arrays.asList(session.getWorkspace().getAccessibleWorkspaceNames());
            } catch (RepositoryException e) {
                throw new JcrException(e);
            } finally {
                closeSession(session, false);
            }
        }
    }
}
