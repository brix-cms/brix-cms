package org.brixcms.plugin.menu.auth;

import org.brixcms.auth.Action;
import org.brixcms.workspace.Workspace;

public class AccessMenuPluginAction implements Action {
    private final Workspace workspace;

    public AccessMenuPluginAction(Workspace workspace) {
        this.workspace = workspace;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public String toString() {
        return "AccessMenuPluginAction{" + "workspace=" + workspace + '}';
    }

    @Override
    public Context getContext() {
        return Context.ADMINISTRATION;
    }
}
