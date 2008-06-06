package brix.plugin.publishing;

import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.Plugin;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.web.admin.navigation.AbstractNavigationTreeNode;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

public class PublishingPlugin implements Plugin
{
    public static final String STATE_DEVELOPMENT = "development";
    public static final String STATE_STAGING = "staging";
    public static final String STATE_PRODUCTION = "production";
    
    private final Brix brix;
    
    public PublishingPlugin(Brix brix)
	{
		this.brix = brix;
	}
    
    public static PublishingPlugin get(Brix brix)
    {
        return (PublishingPlugin)brix.getPlugin(ID);
    }
    
    public static PublishingPlugin get()
    {
        return get(Brix.get());
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
        
        JcrSession sourceSession = brix.getCurrentSession(workspace.getId());
        JcrSession targetSession = brix.getCurrentSession(target.getId());
        
        brix.clone(sourceSession, targetSession);        
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
    
    private static class Node extends AbstractNavigationTreeNode
    {
        public Node(String workspaceId)
        {
            super(workspaceId);
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
                protected ResourceReference getImageResourceReference(BaseTree tree, Object node)
                {
                    return ICON;
                }
            };
        }

        public NavigationAwarePanel< ? > newManagePanel(String id)
        {
            return new PublishingPanel(id, new WorkspaceModel(getWorkspaceId()));
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
