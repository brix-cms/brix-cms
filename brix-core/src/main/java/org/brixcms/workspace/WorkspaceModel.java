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

import org.apache.wicket.model.IModel;
import org.brixcms.Brix;

public class WorkspaceModel implements IModel<Workspace> {
    private String workspaceId;
    private transient Workspace workspace;

    public WorkspaceModel(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public WorkspaceModel(Workspace workspace) {
        if (workspace != null) {
            setObject(workspace);
        }
    }

    public void setObject(Workspace workspace) {
        if (workspace != null) {
            this.workspaceId = workspace.getId();
        } else {
            this.workspaceId = null;
        }
        this.workspace = workspace;
    }



    public void detach() {
        workspace = null;
    }

    public Workspace getObject() {
        if (workspace == null && workspaceId != null) {
            workspace = Brix.get().getWorkspaceManager().getWorkspace(workspaceId);
        }
        return workspace;
    }
}
