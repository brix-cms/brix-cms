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

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.Action.Context;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.workspace.Workspace;
import org.brixcms.workspace.WorkspaceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkspaceSwitcher extends BrixGenericPanel<Workspace> {
    private Map<String, String> workspaceNameCache;

    public WorkspaceSwitcher(String id, IModel<Workspace> model) {
        super(id, model);

        IModel<List<? extends Workspace>> workspaceModel = new LoadableDetachableModel<List<? extends Workspace>>() {
            @Override
            protected List<? extends Workspace> load() {
                return getWorkspaces();
            }
        };
        DropDownChoice<Workspace> choice = new DropDownChoice<Workspace>("workspaces", model, workspaceModel,
                new Renderer()) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(Workspace newSelection) {
                detach();
                super.onSelectionChanged(newSelection);
            }
        };
        choice.setNullValid(false);
        add(choice);
    }

    private List<Workspace> getWorkspaces() {
        Brix brix = getBrix();
        List<Workspace> workspaces = new ArrayList<Workspace>();

        Workspace current = getModelObject();

        for (Plugin p : brix.getPlugins()) {
            List<Workspace> filtered = brix.filterVisibleWorkspaces(p.getWorkspaces(current, false),
                    Context.ADMINISTRATION);
            for (Workspace w : filtered) {
                if (workspaceNameCache == null) {
                    workspaceNameCache = new HashMap<String, String>();
                }
                workspaceNameCache.put(w.getId(), p.getUserVisibleName(w, false));
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

// -------------------------- OTHER METHODS --------------------------
    private String getWorkspaceName(Workspace workspace) {
        if (workspaceNameCache == null) {
            workspaceNameCache = new HashMap<String, String>();
        }
        String name = workspaceNameCache.get(workspace.getId());
        if (name == null) {
            for (Plugin p : getBrix().getPlugins()) {
                if (p.isPluginWorkspace(workspace)) {
                    name = p.getUserVisibleName(workspace, false);
                }
                workspaceNameCache.put(workspace.getId(), name);
            }
        }
        return name;
    }

    private boolean isCurrentWorkspaceValid() {
        WorkspaceManager manager = getBrix().getWorkspaceManager();
        Workspace workspace = getModelObject();
        return workspace != null && manager.workspaceExists(workspace.getId());
    }

    @Override
    protected void onDetach() {
        workspaceNameCache = null;
        super.onDetach();
    }

    private class Renderer implements IChoiceRenderer<Workspace> {
        public Object getDisplayValue(Workspace object) {
            return getWorkspaceName(object);
        }

        public String getIdValue(Workspace object, int index) {
            return object.getId();
        }
    }
}
