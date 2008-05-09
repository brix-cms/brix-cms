package brix.plugin.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Plugin;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.api.JcrSession;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;

public class MenuPlugin implements Plugin
{

    public MenuPlugin()
    {

    }

    private static final String ID = MenuPlugin.class.getName();

    public String getId()
    {
        return ID;
    }

    public static MenuPlugin get(Brix brix)
    {
        return (MenuPlugin)brix.getPlugin(ID);
    }

    public static MenuPlugin get()
    {
        return get(BrixRequestCycle.Locator.getBrix());
    }

    public NavigationTreeNode newNavigationTreeNode(String workspaceName)
    {
        return new Node(workspaceName);
    }

    private static class Node implements NavigationTreeNode
    {
        private final String workspaceName;

        public Node(String workspaceName)
        {
            this.workspaceName = workspaceName;
        }

        public boolean getAllowsChildren()
        {
            return false;
        }

        public TreeNode getChildAt(int childIndex)
        {
            return null;
        }

        public int getChildCount()
        {
            return 0;
        }

        public int getIndex(TreeNode node)
        {
            return -1;
        }

        public TreeNode getParent()
        {
            return null;
        }

        public boolean isLeaf()
        {
            return true;
        }

        public Enumeration< ? > children()
        {
            return Collections.enumeration(Collections.emptyList());
        }

        @Override
        public String toString()
        {
            return "Menus";
        }

        public Panel< ? > newLinkPanel(String id, BaseTree tree)
        {
            return new LinkIconPanel(id, new Model<Node>(this), tree)
            {
                @Override
                protected ResourceReference getImageResourceReference(BaseTree tree, TreeNode node)
                {
                    return ICON;
                }
            };
        }

        public NavigationAwarePanel< ? > newManagePanel(String id)
        {
            return new ManageMenuPanel(id, workspaceName);
        }
    };

    private static final ResourceReference ICON = new ResourceReference(MenuPlugin.class, "icon.png");
    
    private static String ROOT_NODE_NAME = Brix.NS_PREFIX + "menu";

    public String getRootPath()
    {
        return BrixRequestCycle.Locator.getBrix().getRootPath() + "/" + ROOT_NODE_NAME;
    }

    private JcrNode getRootNode(String workspaceName, boolean createIfNotExist)
    {
        JcrSession session = BrixRequestCycle.Locator.getSession(workspaceName);

        if (session.itemExists(getRootPath()) == false)
        {
            if (createIfNotExist)
            {
                JcrNode parent = (JcrNode)session.getItem(BrixRequestCycle.Locator.getBrix()
                    .getRootPath());
                parent.addNode(ROOT_NODE_NAME, "nt:unstructured");
            }
            else
            {
                return null;
            }
        }

        return (JcrNode)session.getItem(getRootPath());
    }

    public List<JcrNode> getMenuNodes(String workspaceName)
    {
        JcrNode root = getRootNode(workspaceName, false);
        if (root != null)
        {
            List<JcrNode> result = new ArrayList<JcrNode>();
            JcrNodeIterator i = root.getNodes("menu");
            while (i.hasNext())
            {
                result.add(i.nextNode());
            }
            return result;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public JcrNode saveMenu(Menu menu, String workspaceName, JcrNode node)
    {
        if (node != null)
        {
            menu.save(node);
        }
        else
        {
            JcrNode root = getRootNode(workspaceName, true);
            node = root.addNode("menu");
            menu.save(node);
        }
        node.getSession().save();
        return node;
    }
    
    public void initWorkspace(JcrSession workspaceSession)
    {
        
    }
}
