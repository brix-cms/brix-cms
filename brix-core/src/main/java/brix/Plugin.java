package brix;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;

import brix.jcr.api.JcrSession;
import brix.registry.ExtensionPoint;
import brix.workspace.Workspace;

public interface Plugin
{
    public static final ExtensionPoint<Plugin> POINT = new ExtensionPoint<Plugin>()
    {

        public Multiplicity getMultiplicity()
        {
            return Multiplicity.COLLECTION;
        }

        public String getUuid()
        {
            return Plugin.class.getName();
        }

    };

    String getId();

    ITab newTab(Workspace workspace);   

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession);

    List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend);

    public String getUserVisibleName(Workspace workspace, boolean isFrontend);
}
