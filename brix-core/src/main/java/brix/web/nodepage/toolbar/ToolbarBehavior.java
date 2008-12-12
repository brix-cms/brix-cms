package brix.web.nodepage.toolbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;
import org.apache.wicket.util.lang.Objects;
import org.apache.wicket.util.string.Strings;

import brix.Brix;
import brix.Plugin;
import brix.auth.Action.Context;
import brix.web.BrixRequestCycleProcessor;
import brix.workspace.Workspace;

public abstract class ToolbarBehavior extends AbstractDefaultAjaxBehavior
{

    public ToolbarBehavior()
    {

    }

    private static final CompressedResourceReference cssReference = new CompressedResourceReference(
            ToolbarBehavior.class, "toolbar.css");
    private static final JavascriptResourceReference javascriptReference = new JavascriptResourceReference(
            ToolbarBehavior.class, "toolbar.js");

    private String escape(String s)
    {
        String res = Strings.escapeMarkup(s).toString();
        res.replace("\\", "\\\\");
        res.replace("'", "\\'");
        return res;
    }

    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);
        response.renderCSSReference(cssReference);
        response.renderJavascriptReference(javascriptReference);

        String defaultWorkspace = getCurrentWorkspaceId();
        List<WorkspaceEntry> workspaces = getWorkspaces();

        String workspaceArray[] = new String[workspaces.size()];
        for (int i = 0; i < workspaces.size(); ++i)
        {
            WorkspaceEntry e = workspaces.get(i);
            workspaceArray[i] = "{ name: '" + escape(e.visibleName) + "', value: '" + e.id + "' }";
        }

        if (defaultWorkspace == null)
        {
            defaultWorkspace = "null";
        }
        else
        {
            defaultWorkspace = "'" + defaultWorkspace + "'";
        }

        response.renderJavascript("BrixToolbarInit(" + Arrays.toString(workspaceArray) + ", " +
                defaultWorkspace + ");", "brix-toolbar-init");
    }

    protected abstract String getCurrentWorkspaceId();

    @Override
    public boolean getStatelessHint(Component component)
    {
        return true;
    }

    private List<WorkspaceEntry> workspaces;

    private List<WorkspaceEntry> loadWorkspaces()
    {
        Brix brix = Brix.get();
        List<WorkspaceEntry> workspaces = new ArrayList<WorkspaceEntry>();
        Workspace currentWorkspace = getCurrentWorkspaceId() != null ? brix.getWorkspaceManager()
                .getWorkspace(getCurrentWorkspaceId()) : null;

        for (Plugin p : brix.getPlugins())
        {
            List<Workspace> filtered = brix.filterVisibleWorkspaces(p.getWorkspaces(
                    currentWorkspace, true), Context.PRESENTATION);
            for (Workspace w : filtered)
            {
                WorkspaceEntry we = new WorkspaceEntry();
                we.id = w.getId();
                we.visibleName = p.getUserVisibleName(w, true);
                workspaces.add(we);
            }
        }

        return workspaces;
    }

    private List<WorkspaceEntry> getWorkspaces()
    {
        if (workspaces == null)
        {
            workspaces = loadWorkspaces();
        }
        return workspaces;
    }

    private static class WorkspaceEntry implements Serializable
    {
        private String id;
        private String visibleName;

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj instanceof WorkspaceEntry)
                return false;
            WorkspaceEntry that = (WorkspaceEntry)obj;
            return Objects.equal(id, that.id);
        }
    };

    @Override
    public boolean isEnabled(Component component)
    {
        if (!Brix.get().getAuthorizationStrategy().isActionAuthorized(
                new AccessWorkspaceSwitcherToolbarAction()))
        {
            return false;
        }

        RequestCycle requestCycle = RequestCycle.get();
        if (requestCycle.getRequest().getParameter(BrixRequestCycleProcessor.WORKSPACE_PARAM) != null)
        {
            return false;
        }
        else
        {
            List<WorkspaceEntry> workspaces = getWorkspaces();
            return workspaces.size() > 1 ||
                    (workspaces.size() == 1 && !workspaces.get(0).id
                            .equals(getCurrentWorkspaceId()));
        }
    }

    @Override
    public void detach(Component component)
    {
        super.detach(component);
        workspaces = null;
    }

    @Override
    protected void respond(AjaxRequestTarget target)
    {

    }
}
