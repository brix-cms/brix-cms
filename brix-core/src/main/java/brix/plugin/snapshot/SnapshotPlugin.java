package brix.plugin.snapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import brix.web.admin.navigation.AbstractNavigationTreeNode;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

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

    private static final String WORKSPACE_TYPE = "brix:snapshot";

    private static final String WORKSPACE_ATTRIBUTE_SITE_NAME = "brix:snapshot-site-name";

    private static final String WORKSPACE_ATTRIBUTE_CREATED = "brix:snapshot-created";

    public boolean isSnapshotWorkspace(Workspace workspace)
    {
        return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
    }

    public String getSnapshotSiteName(Workspace workspace)
    {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_SITE_NAME);
    }

    public void setCreated(Workspace workspace, Date created)
    {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        String formatted = df.format(created);
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_CREATED, formatted);
    }

    public Date getCreated(Workspace workspace)
    {
        String formatted = workspace.getAttribute(WORKSPACE_ATTRIBUTE_CREATED);
        if (formatted == null)
        {
            return null;
        }
        else
        {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
            Date date;
            try
            {
                date = df.parse(formatted);
                return date;
            }
            catch (ParseException e)
            {
                return null;
            }
        }
    }

    public List<Workspace> getSnapshotsForWorkspace(Workspace workspace)
    {
        String siteName = null;

        if (SitePlugin.get().isSiteWorkspace(workspace))
        {
            siteName = SitePlugin.get().getWorkspaceName(workspace);
        }
        else if (SnapshotPlugin.get().isSnapshotWorkspace(workspace))
        {
            siteName = getSnapshotSiteName(workspace);
        }


        if (siteName != null)
        {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
            attributes.put(WORKSPACE_ATTRIBUTE_SITE_NAME, siteName);
            return BrixRequestCycle.Locator.getBrix().getWorkspaceManager().getWorkspacesFiltered(
                attributes);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public void createSnapshot(Workspace workspace)
    {
        if (!SitePlugin.get().isSiteWorkspace(workspace))
        {
            throw new IllegalStateException("Workspace must be a Site workspace");
        }
        Brix brix = BrixRequestCycle.Locator.getBrix();

        Workspace targetWorkspace = brix.getWorkspaceManager().createWorkspace();
        targetWorkspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        targetWorkspace.setAttribute(WORKSPACE_ATTRIBUTE_SITE_NAME, SitePlugin.get()
            .getWorkspaceName(workspace));

        setCreated(targetWorkspace, new Date());

        JcrSession originalSession = BrixRequestCycle.Locator.getSession(workspace.getId());
        ;
        JcrSession targetSession = BrixRequestCycle.Locator.getSession(targetWorkspace.getId());
        brix.clone(originalSession, targetSession);
    }

    public void restoreSnapshot(Workspace snapshotWorkspace, Workspace targetWorkspace)
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        JcrSession sourceSession = BrixRequestCycle.Locator.getSession(snapshotWorkspace.getId());
        JcrSession targetSession = BrixRequestCycle.Locator.getSession(targetWorkspace.getId());
        brix.clone(sourceSession, targetSession);
    }

    public NavigationTreeNode newNavigationTreeNode(Workspace workspace)
    {
        return new Node(workspace.getId());
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
            return "Snapshots";
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
            return new ManageSnapshotsPanel(id, new WorkspaceModel(getWorkspaceId()));
        }
    };

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {

    }

    private static final ResourceReference ICON = new ResourceReference(SnapshotPlugin.class,
        "camera.png");

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        return "Snapshot - " + getSnapshotSiteName(workspace) + " - " + df.format(getCreated(workspace));
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        if (isFrontend)
        {
            return getSnapshotsForWorkspace(currentWorkspace);
        }
        else
        {
            return null;
        }
    }
}
