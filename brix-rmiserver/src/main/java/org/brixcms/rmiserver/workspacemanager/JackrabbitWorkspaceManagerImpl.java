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

package org.brixcms.rmiserver.workspacemanager;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.brixcms.workspace.AbstractSimpleWorkspaceManager;
import org.brixcms.workspace.JcrException;
import org.brixcms.workspace.WorkspaceManager;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of Jackrabbit {@link WorkspaceManager}
 *
 * @author igor.vaynberg
 */
public class JackrabbitWorkspaceManagerImpl extends AbstractSimpleWorkspaceManager {
    private final RepositoryImpl repository;
    private final Credentials credentials;

    /**
     * Constructor
     *
     * @param repository  repository
     * @param credentials repository credentials
     */
    public JackrabbitWorkspaceManagerImpl(RepositoryImpl repository, Credentials credentials) {
        this.repository = repository;
        this.credentials = credentials;
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

    @Override
    protected Session createSession(String workspaceName) {
        try {
            return repository.login(credentials, workspaceName);
        } catch (Exception e) {
            throw new RuntimeException("Could not login into repository", e);
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
