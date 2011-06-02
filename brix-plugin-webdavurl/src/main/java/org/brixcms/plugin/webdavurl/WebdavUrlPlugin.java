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

package org.brixcms.plugin.webdavurl;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.Action;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.web.util.AbstractModel;
import org.brixcms.workspace.Workspace;

import java.util.Arrays;
import java.util.List;

public class WebdavUrlPlugin implements Plugin {

    public String getId() {
        return getClass().getName();
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        return null;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {

    }

    public boolean isPluginWorkspace(Workspace workspace) {
        return false;
    }

    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel) {
        IBrixTab tabs[] = new IBrixTab[]{new Tab(new ResourceModel("webdav", "WebDAV"),
                workspaceModel)};
        return Arrays.asList(tabs);
    }

    static class Tab extends AbstractWorkspaceTab {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 10);
        }

        @Override
        public Panel newPanel(String panelId, final IModel<Workspace> workspaceModel) {
            return new WebdavUrlPanel(panelId, new AbstractModel<String>() {
                @Override
                public String getObject() {
                    return workspaceModel.getObject().getId();
                }
            });
        }

        @Override
        public boolean isVisible() {
            final Brix brix = Brix.get();
            final Workspace workspace = getWorkspaceModel().getObject();
            final Action action = new AccessWebDavUrlPluginAction(workspace);
            final boolean granted = brix.getAuthorizationStrategy().isActionAuthorized(action);
            return granted;
        }
    }

    ;
}
