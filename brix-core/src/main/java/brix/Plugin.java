package brix;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;

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
    
    List<ITab> newTabs(IModel<Workspace> workspaceModel);   

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession);

    List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend);

    /**
     * Returns <code>true</code> if the plugin is responsible for the given workspace. 
     * workspace. E.g. for snapshot workspaces the SnapshotPlugin should return
     * <code>true</code>, all other plugins should return <code>false</code>. 
     * 
     * @param workspace
     * @return
     */
    public boolean isPluginWorkspace(Workspace workspace);
    
    public String getUserVisibleName(Workspace workspace, boolean isFrontend);
}
