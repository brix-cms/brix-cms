package brix.plugin.fragment;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.Model;

import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.workspace.Workspace;

public class FragmentPluginNavigationTreeNode implements NavigationTreeNode
{
    private final String workspaceId;

    public FragmentPluginNavigationTreeNode(Workspace workspace)
    {
        this.workspaceId = workspace.getId();
    }

    public Panel< ? > newLinkPanel(String id, BaseTree tree)
    {
        return new LinkIconPanel(id, new Model<FragmentPluginNavigationTreeNode>(this), tree);
    }

    public NavigationAwarePanel< ? > newManagePanel(String id)
    {
        return new FragmentManagerPanel(id, workspaceId);
    }

    public List< ? extends brix.web.tree.TreeNode> getChildren()
    {
        return Collections.emptyList();
    }

    public boolean isLeaf()
    {
        return true;
    }

    public void detach()
    {
    }

    @Override
    public String toString()
    {
        return "Fragments";
    }

}
