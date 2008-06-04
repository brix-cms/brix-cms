package brix.plugin.fragment;

import brix.web.admin.navigation.NavigationAwarePanel;

public class FragmentManagerPanel extends NavigationAwarePanel
{
    private final String workspaceId;

    public FragmentManagerPanel(String id, String workspaceId)
    {
        super(id);
        this.workspaceId = workspaceId;
    }

}
