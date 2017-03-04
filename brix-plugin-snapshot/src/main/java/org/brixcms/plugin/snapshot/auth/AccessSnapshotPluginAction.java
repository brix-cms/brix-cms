package org.brixcms.plugin.snapshot.auth;

import org.brixcms.auth.Action;
import org.brixcms.workspace.Workspace;

public class AccessSnapshotPluginAction implements Action {
    private final Workspace workspace;

    public AccessSnapshotPluginAction(Workspace workspace) {
        this.workspace = workspace;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public String toString() {
        return "AccessSnapshotPluginAction{" + "workspace=" + workspace + '}';
    }


    public Context getContext() {
        return Context.ADMINISTRATION;
    }
}
