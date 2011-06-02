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

package org.brixcms.web.tab;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceModel;

public abstract class AbstractWorkspaceTab extends CachingAbstractTab implements IDetachable {
    private final IModel<Workspace> workspaceModel;

    public AbstractWorkspaceTab(IModel<String> title, Workspace workspace) {
        this(title, new WorkspaceModel(workspace), 0);
    }

    public AbstractWorkspaceTab(IModel<String> title, IModel<Workspace> workspaceModel) {
        super(title, 0);
        this.workspaceModel = workspaceModel;
    }

    public AbstractWorkspaceTab(IModel<String> title, Workspace workspace, int priority) {
        this(title, new WorkspaceModel(workspace), priority);
    }

    public AbstractWorkspaceTab(IModel<String> title, IModel<Workspace> workspaceModel, int priority) {
        super(title, priority);
        this.workspaceModel = workspaceModel;
    }

    public IModel<Workspace> getWorkspaceModel() {
        return workspaceModel;
    }


    public void detach() {
        workspaceModel.detach();
    }

    @Override
    public Panel newPanel(String panelId) {
        return newPanel(panelId, workspaceModel);
    }

    public abstract Panel newPanel(String panelId, IModel<Workspace> workspaceModel);
}
