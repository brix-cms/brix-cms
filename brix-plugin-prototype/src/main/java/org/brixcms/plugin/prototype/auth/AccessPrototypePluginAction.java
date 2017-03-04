package org.brixcms.plugin.prototype.auth;

import org.brixcms.auth.Action;
import org.brixcms.workspace.Workspace;

public class AccessPrototypePluginAction implements Action {
    private final Workspace workspace;

    public AccessPrototypePluginAction(Workspace workspace) {
        this.workspace = workspace;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public String toString() {
        return "AccessPrototypePluginAction{" + "workspace=" + workspace + '}';
    }


    public Context getContext() {
        return Context.ADMINISTRATION;
    }
}
