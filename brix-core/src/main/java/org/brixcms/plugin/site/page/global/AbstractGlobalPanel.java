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

package org.brixcms.plugin.site.page.global;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.workspace.Workspace;

public abstract class AbstractGlobalPanel extends BrixGenericPanel<BrixNode> {
    private static final String PANEL_ID = "managePanel";
    IModel<Workspace> workspaceModel;

    public AbstractGlobalPanel(String id, IModel<Workspace> workspaceModel) {
        super(id, new BrixNodeModel(getContainerNode(workspaceModel.getObject())));

        this.workspaceModel = workspaceModel;
    }

    private static BrixNode getContainerNode(Workspace workspace) {
        JcrSession session = Brix.get().getCurrentSession(workspace.getId());
        return SitePlugin.get().getGlobalContainer(session);
    }

    @Override
    protected void onBeforeRender() {
        boolean isInvalidWorkspace = !getModelObject().getSession().getWorkspace().getName().equals(workspaceModel.getObject().getId());
        if (!hasBeenRendered()) {
            add(newManagePanel(PANEL_ID, getModel()));
        } else if (isInvalidWorkspace) {
            setModelObject(getContainerNode(workspaceModel.getObject()));
            get(PANEL_ID).replaceWith(newManagePanel(PANEL_ID, getModel()));
        }
        super.onBeforeRender();
    }

    protected abstract Panel newManagePanel(String id, IModel<BrixNode> containerNodeModel);
}
