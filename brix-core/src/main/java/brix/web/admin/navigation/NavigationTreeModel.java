package brix.web.admin.navigation;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Plugin;
import brix.web.tree.AbstractTreeModel;
import brix.web.tree.TreeNode;
import brix.workspace.Workspace;

public class NavigationTreeModel extends AbstractTreeModel
{

    private RootNode rootNode = new RootNode();

    public Object getRoot()
    {
        return rootNode;
    }
    
    private class RootNode implements NavigationTreeNode
    {

        private List<NavigationTreeNode> children;

        public List<NavigationTreeNode> getChildren()
        {
            if (children == null)
            {
                children = new ArrayList<NavigationTreeNode>();
                Brix brix = BrixRequestCycle.Locator.getBrix();
                for (Plugin p : brix.getPlugins())
                {
                    Workspace workspace = BrixRequestCycle.Locator.getBrix().getWorkspaceManager().getWorkspace(workspaceId);
                    NavigationTreeNode node = p.newNavigationTreeNode(workspace);
                    if (node != null)
                    {
                        children.add(node);
                    }
                }
            }
            return children;
        }

        public TreeNode getChildAt(int childIndex)
        {
            return getChildren().get(childIndex);
        }

        public int getChildCount()
        {
            return getChildren().size();
        }

        public int getChildIndex(TreeNode node)
        {
            return getChildren().indexOf(node);
        }

        public boolean isLeaf()
        {
            return false;
        }

        public void detach()
        {
            
        }

        public Panel< ? > newLinkPanel(String id, BaseTree tree)
        {
            return null;
        }

        public NavigationAwarePanel< ? > newManagePanel(String id)
        {
            return null;
        }
    };

    private final String workspaceId;

    public NavigationTreeModel(String workspaceName)
    {
        this.workspaceId = workspaceName;
    }

}
