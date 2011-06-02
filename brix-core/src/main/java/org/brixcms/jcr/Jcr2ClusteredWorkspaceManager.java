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


import org.brixcms.workspace.AbstractClusteredWorkspaceManager;
import org.brixcms.workspace.JcrException;
import org.brixcms.workspace.WorkspaceManager;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Arrays;
import java.util.List;

/**
 * JCR2 specific implementation of {@link WorkspaceManager}
 *
 * @author igor.vaynberg
 * @author kbachl
 */
public class Jcr2ClusteredWorkspaceManager extends AbstractClusteredWorkspaceManager {
    private final JcrSessionFactory sf;

    /**
     * Construction
     *
     * @param sf session factory that will be used to feed workspace manager sessions
     */
    public Jcr2ClusteredWorkspaceManager(JcrSessionFactory sf) {
        super();
        this.sf = sf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createWorkspace(String workspaceName) {
        Session session = createSession(null);
        try {
            session.getWorkspace().createWorkspace(workspaceName);
        }
        catch (RepositoryException e) {
            throw new JcrException(e);
        }
        finally {
            session.logout();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Session createSession(String workspaceName) {
        return sf.createSession(workspaceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getAccessibleWorkspaceIds() {
        Session session = createSession(null);
        try {
            return Arrays.asList(session.getWorkspace().getAccessibleWorkspaceNames());
        }
        catch (RepositoryException e) {
            throw new JcrException(e);
        }
        finally {
            session.logout();
        }
    }
}