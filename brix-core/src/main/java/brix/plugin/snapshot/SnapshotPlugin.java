package brix.plugin.snapshot;

import java.util.ArrayList;
import java.util.Calendar;
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
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;

public class SnapshotPlugin implements Plugin
{

    private static final String ID = SnapshotPlugin.class.getName();
    
    public String getId()
    {
        return ID;
    }
    
    public static SnapshotPlugin get(Brix brix)
    {
        return (SnapshotPlugin)brix.getPlugin(ID);
    }
    
    public static SnapshotPlugin get()
    {
        return get(BrixRequestCycle.Locator.getBrix());
    }
    
    static final String PREFIX = "snapshot";  
    
    public List<String> getSnapshotsForWorkspace(String workspaceId)
    {
        List<String> res = new ArrayList<String>();
        
        List<String> workspaces = BrixRequestCycle.Locator.getBrix().getAvailableWorkspacesFiltered(PREFIX, workspaceId, null);
        
        for (String s : workspaces)
        {
            res.add(BrixRequestCycle.Locator.getBrix().getWorkspaceResolver().getWorkspaceState(s));
        }
        
        return res;
    }
    
    public void createSnapshot(String workspaceName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        
        Calendar cal = Calendar.getInstance();
        String snapshotSuffix = "" + System.currentTimeMillis();
        String id = brix.getWorkspaceResolver().getWorkspaceId(workspaceName);
        String snapshotName = brix.getWorkspaceResolver().getWorkspaceName(PREFIX, id, snapshotSuffix);
        
        JcrSession originalSession = BrixRequestCycle.Locator.getSession(workspaceName);
        brix.createWorkspace(originalSession, snapshotName);
        
        JcrSession destSession = BrixRequestCycle.Locator.getSession(snapshotName);
        brix.clone(originalSession, destSession);
    }
    
    public void restoreSnapshot(String snapshotWorkspaceName)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        String id = brix.getWorkspaceResolver().getWorkspaceId(snapshotWorkspaceName);
        
        String targetWorkspace = brix.getWorkspaceResolver().getWorkspaceName("site", id, Brix.STATE_DEVELOPMENT);
        JcrSession sourceSession = BrixRequestCycle.Locator.getSession(snapshotWorkspaceName);
        if (brix.getAvailableWorkspaces(sourceSession).contains(targetWorkspace) == false)
        {
            brix.createWorkspace(sourceSession, targetWorkspace);
        }
        JcrSession targetSession = BrixRequestCycle.Locator.getSession(targetWorkspace);
        brix.clone(sourceSession, targetSession);
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
            return "Snapshots";
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
            return new ManageSnapshotsPanel(id, workspaceName);
        }
    };

    private static final ResourceReference ICON = new ResourceReference(SnapshotPlugin.class, "camera.png");


}
