package brix.plugin.site;

import javax.swing.tree.TreeNode;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.jcr.api.JcrNode;
import brix.plugin.site.admin.NodeManagerContainerPanel;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.web.tree.AbstractTreeNode;

public class SiteNavigationTreeNode extends AbstractTreeNode implements NavigationTreeNode
{
    public SiteNavigationTreeNode(IModel<JcrNode> nodeModel)
    {
        super(nodeModel);
    }

    public SiteNavigationTreeNode(JcrNode node)
    {
        super(node);
    }


    public TreeNode getParent()
    {
        // we don't need to worry about the root, model will make sure that this method is not
        // called for the top level node
        JcrNode node = getNodeModel().getObject();
        return new SiteNavigationTreeNode(node.getParent());
    }

    
    @Override
    public String toString()
    {
        JcrNode node = getNodeModel().getObject();
        Brix brix = BrixRequestCycle.Locator.getBrix();
        if (node.getPath().equals(brix.getWebPath())) 
        {
            return "Site";
        }
        else
        {
            return node.getName();
        }
    }
    
    @Override
    protected AbstractTreeNode newTreeNode(JcrNode node)
    {
        return new SiteNavigationTreeNode(node);
    }

    public Panel newLinkPanel(String id, BaseTree tree)
    {
        return new LinkIconPanel(id, new Model(this), tree);
    }

    public NavigationAwarePanel newManagePanel(String id)
    {
        return new NodeManagerContainerPanel(id, getNodeModel());
    }
}
