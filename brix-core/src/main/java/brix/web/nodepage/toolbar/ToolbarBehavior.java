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

import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.auth.WorkspaceAction;
import brix.auth.impl.WorkspaceActionImpl;

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
        List<String> workspaces = getWorkspaces();

        String workspaceArray[] = workspaces.toArray(new String[workspaces.size()]);
        for (int i = 0; i < workspaceArray.length; ++i)
        {
            workspaceArray[i] = "'" + workspaceArray[i] + "'";
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

    private List<String> workspaces;

    public List<String> getWorkspaces()
    {
        if (workspaces == null)
        {
            workspaces = filterWorkspaces(Locator.getBrix().getAvailableWorkspaces());
        }
        return workspaces;
    }

    private List<String> filterWorkspaces(List<String> workspaces)
    {
        List<String> result = new ArrayList<String>(workspaces.size());
        for (String s : workspaces)
        {
            Action action = new WorkspaceActionImpl(Action.Context.PRESENTATION,
                    WorkspaceAction.Type.VIEW, s);
            if (Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action))
            {
                result.add(s);
            }
        }

        return result;
    }

    @Override
    public boolean isEnabled(Component< ? > component)
    {
        List<String> workspaces = getWorkspaces();
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
