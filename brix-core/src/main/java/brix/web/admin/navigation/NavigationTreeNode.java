package brix.web.admin.navigation;

import java.io.Serializable;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;

import brix.web.tree.TreeNode;

public interface NavigationTreeNode extends TreeNode, Serializable
{
    public Panel<?> newLinkPanel(String id, BaseTree tree);
    
    public NavigationAwarePanel<?> newManagePanel(String id);
}
