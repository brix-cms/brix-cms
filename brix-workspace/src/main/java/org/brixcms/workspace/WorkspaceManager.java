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

import java.util.List;
import java.util.Map;

/**
 * Workspace manager is a think layer on top of JCR extending the JCR workspace management functionality. It allows to
 * delete workspaces, set workspace attributes and filter workspaces according to the workspaces.
 *
 * @author Matej Knopp
 */
public interface WorkspaceManager {
    /**
     * Creates a new workspace.
     *
     * @return
     */
    public Workspace createWorkspace();

    /**
     * Returns the {@link Workspace} object associated with the workspace with given id. The workspace id actually a JCR
     * workspace name. If the workspace does not exist <code>null</code> is returned.
     *
     * @param workspaceId
     * @return
     */
    public Workspace getWorkspace(String workspaceId);

    /**
     * Returns the list of all available workspaces.
     *
     * @return
     */
    public List<Workspace> getWorkspaces();

    /**
     * Returns a filtered list of workspaces. Each workspace in the resulting list must have all specified workspace
     * attributes set and the attributes values must be equal to the values in the <code>workspaceAttributes</code>map.
     *
     * @param workspaceAttributes
     * @return
     */
    public List<Workspace> getWorkspacesFiltered(Map<String, String> workspaceAttributes);

    /**
     * Returns whether a workspace with given id exists.
     *
     * @param workspaceId
     * @return
     */
    public boolean workspaceExists(String workspaceId);
}
