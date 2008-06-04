package brix.plugin.fragment;

import java.util.List;

import brix.Plugin;
import brix.jcr.api.JcrSession;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.workspace.Workspace;

public class FragmentPlugin implements Plugin
{

    public String getId()
    {
        return FragmentPlugin.class.getName();
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        return null;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {
        // noop
    }

    public NavigationTreeNode newNavigationTreeNode(Workspace workspace)
    {
        return new FragmentPluginNavigationTreeNode(workspace);
    }


}
