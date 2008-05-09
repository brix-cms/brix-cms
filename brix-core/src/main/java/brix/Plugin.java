package brix;

import brix.jcr.api.JcrSession;
import brix.web.admin.navigation.NavigationTreeNode;

public interface Plugin
{
    String getId();
    
    NavigationTreeNode newNavigationTreeNode(String workspaceName);
    
    public void initWorkspace(JcrSession workspaceSession);
}
