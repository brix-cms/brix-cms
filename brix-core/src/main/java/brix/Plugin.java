package brix;

import javax.swing.tree.TreeNode;

import brix.web.admin.navigation.NavigationTreeNode;

public interface Plugin
{
    String getId();
    
    NavigationTreeNode newNavigationTreeNode(String workspaceName);
}
