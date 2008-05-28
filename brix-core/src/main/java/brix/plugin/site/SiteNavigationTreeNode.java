package brix.plugin.site;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.admin.NodeManagerContainerPanel;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.web.tree.AbstractJcrTreeNode;

public class SiteNavigationTreeNode extends AbstractJcrTreeNode implements NavigationTreeNode
{
    public SiteNavigationTreeNode(IModel<BrixNode> nodeModel)
    {
        super(nodeModel);
    }

    public SiteNavigationTreeNode(BrixNode node)
    {
        super(node);
    }
    
    @Override
    public String toString()
    {
        BrixNode node = getNodeModel().getObject();
        if (node.getPath().equals(SitePlugin.get().getSiteRootPath())) 
        {
            return "Site";
        }
        else
        {
            return node.getName();
        }
    }
    
    @Override
    protected AbstractJcrTreeNode newTreeNode(BrixNode node)
    {
        return new SiteNavigationTreeNode(node);
    }

    public Panel<?> newLinkPanel(String id, BaseTree tree)
    {
        return new LinkIconPanel(id, new Model<SiteNavigationTreeNode>(this), tree);
    }

    public NavigationAwarePanel<?> newManagePanel(String id)
    {
        return new NodeManagerContainerPanel(id, getNodeModel());
    }
}
