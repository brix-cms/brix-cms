package brix.web.admin.navigation;

import org.apache.wicket.markup.html.tree.BaseTree;

public interface Navigation
{
    public BaseTree getTree();
    
    public void selectNode(NavigationTreeNode node);
    
    public void nodeChanged(NavigationTreeNode node);
    
    public void nodeInserted(NavigationTreeNode node);
    
    public void nodeDeleted(NavigationTreeNode node);
    
    public void nodeChildrenChanged(NavigationTreeNode node);       
}
