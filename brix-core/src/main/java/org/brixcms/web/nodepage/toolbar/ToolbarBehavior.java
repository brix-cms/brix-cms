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

package org.brixcms.web.nodepage.toolbar;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.string.Strings;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.auth.Action.Context;
import org.brixcms.web.BrixRequestCycleProcessor;
import org.brixcms.workspace.Workspace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ToolbarBehavior extends AbstractDefaultAjaxBehavior {
// ------------------------------ FIELDS ------------------------------

    private static final CompressedResourceReference cssReference = new CompressedResourceReference(
            ToolbarBehavior.class, "toolbar.css");
    private static final JavascriptResourceReference javascriptReference = new JavascriptResourceReference(
            ToolbarBehavior.class, "toolbar.js");

    private List<WorkspaceEntry> workspaces;

// --------------------------- CONSTRUCTORS ---------------------------

    public ToolbarBehavior() {

    }

// --------------------- GETTER / SETTER METHODS ---------------------

    private List<WorkspaceEntry> getWorkspaces() {
        if (workspaces == null) {
            workspaces = loadWorkspaces();
        }
        return workspaces;
    }

    private List<WorkspaceEntry> loadWorkspaces() {
        Brix brix = Brix.get();
        List<WorkspaceEntry> workspaces = new ArrayList<WorkspaceEntry>();
        Workspace currentWorkspace = getCurrentWorkspaceId() != null ? brix.getWorkspaceManager()
                .getWorkspace(getCurrentWorkspaceId()) : null;

        for (Plugin p : brix.getPlugins()) {
            List<Workspace> filtered = brix.filterVisibleWorkspaces(p.getWorkspaces(
                    currentWorkspace, true), Context.PRESENTATION);
            for (Workspace w : filtered) {
                WorkspaceEntry we = new WorkspaceEntry();
                we.id = w.getId();
                we.visibleName = p.getUserVisibleName(w, true);
                workspaces.add(we);
            }
        }

        return workspaces;
    }

    protected abstract String getCurrentWorkspaceId();

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface IBehavior ---------------------


    @Override
    public void detach(Component component) {
        super.detach(component);
        workspaces = null;
    }

    @Override
    public boolean getStatelessHint(Component component) {
        return true;
    }

    ;

    @Override
    public boolean isEnabled(Component component) {
        if (!Brix.get().getAuthorizationStrategy().isActionAuthorized(
                new AccessWorkspaceSwitcherToolbarAction())) {
            return false;
        }

        RequestCycle requestCycle = RequestCycle.get();
        if (requestCycle.getRequest().getParameter(BrixRequestCycleProcessor.WORKSPACE_PARAM) != null) {
            return false;
        } else {
            List<WorkspaceEntry> workspaces = getWorkspaces();
            return workspaces.size() > 1 ||
                    (workspaces.size() == 1 && !workspaces.get(0).id
                            .equals(getCurrentWorkspaceId()));
        }
    }

// --------------------- Interface IHeaderContributor ---------------------

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(cssReference);
        response.renderJavascriptReference(javascriptReference);

        String defaultWorkspace = getCurrentWorkspaceId();
        List<WorkspaceEntry> workspaces = getWorkspaces();

        String workspaceArray[] = new String[workspaces.size()];
        for (int i = 0; i < workspaces.size(); ++i) {
            WorkspaceEntry e = workspaces.get(i);
            workspaceArray[i] = "{ name: '" + escape(e.visibleName) + "', value: '" + e.id + "' }";
        }

        if (defaultWorkspace == null) {
            defaultWorkspace = "null";
        } else {
            defaultWorkspace = "'" + defaultWorkspace + "'";
        }

        response.renderJavascript("BrixToolbarInit(" + Arrays.toString(workspaceArray) + ", " +
                defaultWorkspace + ");", "brix-toolbar-init");
    }

// -------------------------- OTHER METHODS --------------------------

    private String escape(String s) {
        String res = Strings.escapeMarkup(s).toString();
        res.replace("\\", "\\\\");
        res.replace("'", "\\'");
        return res;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {

    }

// -------------------------- INNER CLASSES --------------------------

    private static class WorkspaceEntry implements Serializable {
        private String id;
        private String visibleName;

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj instanceof WorkspaceEntry)
                return false;
            WorkspaceEntry that = (WorkspaceEntry) obj;
            return Objects.equal(id, that.id);
        }
    }
}
