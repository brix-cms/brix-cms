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

package brix.jcr;


import java.util.Arrays;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import brix.workspace.AbstractClusteredWorkspaceManager;
import brix.workspace.JcrException;
import brix.workspace.WorkspaceManager;

/**
 * Jackrabbit specific implementation of {@link WorkspaceManager}
 * 
 * @author igor.vaynberg
 * 
 */
public class JackrabbitClusteredWorkspaceManager extends AbstractClusteredWorkspaceManager
{

    private final JcrSessionFactory sf;

    /**
     * Construction
     * 
     * @param sf
     *            session factory that will be used to feed workspace manager sessions
     */
    public JackrabbitClusteredWorkspaceManager(JcrSessionFactory sf)
    {
        super();
        this.sf = sf;
    }

    /** {@inheritDoc} */
    @Override
    protected void createWorkspace(String workspaceName)
    {
        Session session = createSession(null);
        try
        {
        	org.apache.jackrabbit.core.WorkspaceImpl workspace = (org.apache.jackrabbit.core.WorkspaceImpl) session
					.getWorkspace();
            workspace.createWorkspace(workspaceName);
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
        finally
        {
            session.logout();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected List<String> getAccessibleWorkspaceIds()
    {
        Session session = createSession(null);
        try
        {
            return Arrays.asList(session.getWorkspace().getAccessibleWorkspaceNames());
        }
        catch (RepositoryException e)
        {
            throw new JcrException(e);
        }
        finally
        {
            session.logout();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Session createSession(String workspaceName)
    {
        return sf.createSession(workspaceName);
    }

}