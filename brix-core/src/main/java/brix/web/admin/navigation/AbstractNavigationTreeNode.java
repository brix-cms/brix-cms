package brix.web.admin.navigation;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;

import brix.web.tree.TreeNode;

public class AbstractNavigationTreeNode implements NavigationTreeNode
{

    public Panel< ? > newLinkPanel(String id, BaseTree tree)
    {
        return null;
    }

    public NavigationAwarePanel< ? > newManagePanel(String id)
    {
        return null;
    }

    public List< ? extends TreeNode> getChildren()
    {
        return null;
    }

    public boolean isLeaf()
    {
        return true;
    }

    public void detach()
    {
    }

    public AbstractNavigationTreeNode(String workspaceId)
    {
        this.workspaceId = workspaceId;
    }

    public String getWorkspaceId()
    {
        return workspaceId;
    }

    private final String workspaceId;
}
