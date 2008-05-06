package brix.web.nodepage.toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavascriptResourceReference;

import brix.Plugin;
import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.auth.WorkspaceAction;
import brix.auth.impl.WorkspaceActionImpl;
import brix.web.nodepage.toolbar.WorkspaceListProvider.Entry;

public abstract class ToolbarBehavior extends AbstractDefaultAjaxBehavior
{

    public ToolbarBehavior()
    {

    }

    private static final CompressedResourceReference cssReference = new CompressedResourceReference(
        ToolbarBehavior.class, "toolbar.css");
    private static final JavascriptResourceReference javascriptReference = new JavascriptResourceReference(
        ToolbarBehavior.class, "toolbar.js");

    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);
        response.renderCSSReference(cssReference);
        response.renderJavascriptReference(javascriptReference);

        String defaultWorkspace = getWorkspaceName();
        List<Entry> workspaces = getWorkspaces();

        String workspaceArray[] = new String[workspaces.size()];
        for (int i = 0; i < workspaces.size(); ++i)
        {
            Entry e = workspaces.get(i);
            workspaceArray[i] = "{ name: '" + e.userVisibleName + "', value: '" + e.workspaceName +
                "' }";
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

    protected abstract String getWorkspaceName();

    @Override
    public boolean getStatelessHint(Component component)
    {
        return true;
    }

    private List<Entry> workspaces;

    public List<Entry> getWorkspaces()
    {
        if (workspaces == null)
        {
            workspaces = new ArrayList<Entry>();
            for (Plugin p : Locator.getBrix().getPlugins())
            {
                if (p instanceof WorkspaceListProvider)
                {
                    workspaces.addAll(filterWorkspaces(((WorkspaceListProvider)p)
                        .getVisibleWorkspaces(getWorkspaceName())));
                }
            }
        }
        return workspaces;
    }

    private List<Entry> filterWorkspaces(List<Entry> workspaces)
    {
        List<Entry> result = new ArrayList<Entry>(workspaces.size());
        for (Entry e : workspaces)
        {
            Action action = new WorkspaceActionImpl(Action.Context.PRESENTATION,
                WorkspaceAction.Type.VIEW, e.workspaceName);
            if (Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action))
            {
                result.add(e);
            }
        }

        return result;
    }

    @Override
    public boolean isEnabled(Component< ? > component)
    {
        List<Entry> workspaces = getWorkspaces();
        return workspaces.size() > 1 ||
            (workspaces.size() == 1 && workspaces.get(0).equals(getWorkspaceName()));
    }

    @Override
    public void detach(Component< ? > component)
    {
        super.detach(component);
        workspaces = null;
    }

    @Override
    protected void respond(AjaxRequestTarget target)
    {

    }
}
