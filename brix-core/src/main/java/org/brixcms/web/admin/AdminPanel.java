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

package org.brixcms.web.admin;

import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.Action.Context;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.tab.BrixTabbedPanel;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceManager;
import org.brixcms.workspace.WorkspaceModel;

import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends BrixGenericPanel<Workspace> implements IHeaderContributor {
    private static final ResourceReference CSS = new CssResourceReference(AdminPanel.class,
            "res/style.css");

    private TabbedPanel tabbedPanel;
    private final WebMarkupContainer container;
    private WebMarkupContainer noWorkspacesContainer;

    public AdminPanel(String id, String workspace) {
        super(id);
        setModel(new WorkspaceModel(workspace));

        add(container = new WebMarkupContainer("container") {
            @Override
            public boolean isVisible() {
                return AdminPanel.this.getModelObject() != null;
            }
        });
    }

    private List<Workspace> getWorkspaces() {
        Brix brix = getBrix();
        List<Workspace> workspaces = new ArrayList<Workspace>();

        Workspace current = getModelObject();

        for (Plugin p : brix.getPlugins()) {
            List<Workspace> filtered = brix.filterVisibleWorkspaces(
                    p.getWorkspaces(current, false), Context.ADMINISTRATION);
            for (Workspace w : filtered) {
                workspaces.add(w);
            }
        }

        if (!workspaces.contains(current)) {
            workspaces.add(current);
        }
        return workspaces;
    }

    private Brix getBrix() {
        return Brix.get();
    }


    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(CSS);
    }

    private List<Workspace> getAvailableWorkspaces() {
        Brix brix = Brix.get();
        List<Workspace> workspaces = new ArrayList<Workspace>();

        Workspace current = getModelObject();

        for (Plugin p : brix.getPlugins()) {
            List<Workspace> filtered = brix.filterVisibleWorkspaces(
                    p.getWorkspaces(current, false), Context.ADMINISTRATION);
            for (Workspace w : filtered) {
                workspaces.add(w);
            }
        }

        if (!workspaces.contains(current) && current != null) {
            workspaces.add(current);
        }
        return workspaces;
    }

    @Override
    protected void onBeforeRender() {
        fixCurrentWorkspace();

        if (noWorkspacesContainer == null) {
            add(noWorkspacesContainer = newNoWorkspacesContainer("no-workspaces"));
        }
        noWorkspacesContainer.setVisible(!container.determineVisibility());


        if (tabbedPanel == null && container.determineVisibility()) {
            container.add(newWorkspaceSwitcher("switcher", getModel()));
            setupTabbedPanel();
        }

        super.onBeforeRender();
    }

    private void fixCurrentWorkspace() {
        if (!isCurrentWorkspaceValid()) {
            List<Workspace> workspaces = getWorkspaces();
            if (!workspaces.isEmpty()) {
                setModelObject(workspaces.iterator().next());
            }
        }
    }

    private boolean isCurrentWorkspaceValid() {
        WorkspaceManager manager = getBrix().getWorkspaceManager();
        Workspace workspace = getModelObject();
        return workspace != null && manager.workspaceExists(workspace.getId());
    }

    /**
     * Factory method for a container that will display the "no workspaces found" message. This component usually
     * provides its own markup so it is best to use a {@link Panel} or a {@link Fragment}
     *
     * @param id
     * @return
     */
    private WebMarkupContainer newNoWorkspacesContainer(String id) {
        return new WebMarkupContainer(id);
    }

    protected Panel newWorkspaceSwitcher(String id, IModel<Workspace> workspaceModel) {
        return new WorkspaceSwitcher(id, workspaceModel);
    }

    private void setupTabbedPanel() {
        if (tabbedPanel != null) {
            tabbedPanel.remove();
        }

        List<IBrixTab> tabs = new ArrayList<IBrixTab>();

        Brix brix = Brix.get();
        for (Plugin p : brix.getPlugins()) {
            List<IBrixTab> pluginTabs = p.newTabs(getModel());
            if (pluginTabs != null) {
                tabs.addAll(pluginTabs);
            }
        }

        tabbedPanel = new BrixTabbedPanel("tabbedPanel", tabs) {
            @Override
            protected String getTabContainerCssClass() {
                return "brix-plugins-tabbed-panel-row";
            }
        };
        container.add(tabbedPanel);
    }
}
