package org.brixcms.plugin.site.auth;

import org.brixcms.auth.Action;
import org.brixcms.workspace.Workspace;

public class AccessSitePluginAction implements Action {
    private final Workspace workspace;

    public AccessSitePluginAction(Workspace workspace) {
        this.workspace = workspace;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public String toString() {
        return "AccessSitePluginAction{" + "workspace=" + workspace + '}';
    }


    public Context getContext() {
        return Context.ADMINISTRATION;
    }
}
