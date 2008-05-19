package brix.plugin.publishing;

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
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

public class PublishingPlugin implements Plugin
{
    public static final String STATE_DEVELOPMENT = "development";
    public static final String STATE_STAGING = "staging";
    public static final String STATE_PRODUCTION = "production";
    
    public static PublishingPlugin get(Brix brix)
    {
        return (PublishingPlugin)brix.getPlugin(ID);
    }
    
    public static PublishingPlugin get()
    {
        return get(BrixRequestCycle.Locator.getBrix());
    }
    
    public void publish(Workspace workspace, String targetState)
    {       
        if (workspace == null)
        {
            throw new IllegalArgumentException("Argument 'workspace' may not be null.");
        }
        if (targetState == null)
        {
            throw new IllegalArgumentException("Argument 'targetState' may not be null.");
        }
        
        SitePlugin sitePlugin = SitePlugin.get();
        
        if (!sitePlugin.isSiteWorkspace(workspace))
        {
            throw new IllegalStateException("Workspace must be a Site workspace.");
        }
        if (targetState.equals(sitePlugin.getWorkspaceState(workspace)))
        {
            throw new IllegalStateException("Cannot publish workspace to same state it is already.");
        }
        
        String name = sitePlugin.getWorkspaceName(workspace);
        Workspace target = sitePlugin.getSiteWorkspace(name, targetState);
        if (target == null)
        {
            target = sitePlugin.createSite(name, targetState);
        }
        
        JcrSession sourceSession = BrixRequestCycle.Locator.getSession(workspace.getId());
        JcrSession targetSession = BrixRequestCycle.Locator.getSession(target.getId());
        
        BrixRequestCycle.Locator.getBrix().clone(sourceSession, targetSession);        
    }
    
    private static String ID = PublishingPlugin.class.getName();
    
    public String getId()
    {
        return ID;
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        return null;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {
    }
    
    private static class Node implements NavigationTreeNode
    {
        private final String workspaceId;

        public Node(String workspaceId)
        {
            this.workspaceId = workspaceId;
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
            return "Publish";
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
            return new PublishingPanel(id, new WorkspaceModel(workspaceId));
        }
    };

    private static final ResourceReference ICON = new ResourceReference(PublishingPanel.class, "page_white_go.png"); 
    
    public NavigationTreeNode newNavigationTreeNode(Workspace workspace)
    {
        if (SitePlugin.get().isSiteWorkspace(workspace))
        {
            return new Node(workspace.getId());
        }
        else
        {
            return null;
        }
        
    }

}
