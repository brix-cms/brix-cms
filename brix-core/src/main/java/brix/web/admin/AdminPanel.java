package brix.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Objects;

import brix.Brix;
import brix.BrixRequestCycle;
import brix.Path;
import brix.Plugin;
import brix.auth.Action.Context;
import brix.jcr.api.JcrSession;
import brix.web.admin.navigation.Navigation;
import brix.web.admin.navigation.NavigationContainer;
import brix.web.admin.navigation.NavigationPanel;
import brix.web.admin.navigation.NavigationTreeModel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.web.tree.TreeNode;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class AdminPanel extends Panel<Void> implements NavigationContainer
{

    private Component< ? > content;
    private NavigationPanel navigation;

    private WorkspaceEntry currentWorkspace;

    public String getWorkspace()
    {
        return currentWorkspace != null ? currentWorkspace.id : null;
    }

    public void setWorkspace(String workspaceId, String label)
    {
        WorkspaceEntry entry = new WorkspaceEntry();
        entry.id = workspaceId;
        entry.visibleName = label;

        if (entry.equals(currentWorkspace) == false)
        {
            currentWorkspace = entry;
            setupNavigation();
        }
    }

    public void setWorkspace(String workspace)
    {
        setWorkspace(workspace, null);
    }

    public Navigation getNavigation()
    {
        return navigation;
    }

    private void setupNavigation()
    {
        if (navigation != null)
        {
            navigation.remove();
        }
        navigation = new NavigationPanel("navigation", getWorkspace())
        {
            @Override
            protected void onNodeSelected(NavigationTreeNode node)
            {
                AdminPanel.this.onNodeSelected(node);
            }
        };
        add(navigation);

        NavigationTreeModel model = (NavigationTreeModel)navigation.getTree().getModel()
            .getObject();
        TreeNode root = (TreeNode)model.getRoot();
        NavigationTreeNode node;
        if (!root.getChildren().isEmpty())
        {
            node = (NavigationTreeNode)root.getChildren().get(0);
            navigation.selectNode(node);
        }
        else
        {
            node = null;
            onNodeSelected(node);
        }
    }

    private void onNodeSelected(NavigationTreeNode node)
    {
        if (content != null)
        {
            content.remove();
        }
        if (node != null)
        {
            content = node.newManagePanel("content");
        }
        else
        {
            content = new WebMarkupContainer<Void>("content");
        }
        add(content);
    }

    public AdminPanel(String id, String workspace, Path root)
    {
        super(id);

        IModel<WorkspaceEntry> model = new PropertyModel<WorkspaceEntry>(this, "currentWorkspace");
        IChoiceRenderer<WorkspaceEntry> renderer = new ChoiceRenderer<WorkspaceEntry>("visibleName");
        DropDownChoice<WorkspaceEntry> ws = new DropDownChoice<WorkspaceEntry>("workspace", model,
            workspaceEntriesModel, renderer)
        {
            @Override
            protected boolean wantOnSelectionChangedNotifications()
            {
                return true;
            }

            @Override
            protected void onSelectionChanged(WorkspaceEntry newSelection)
            {
                detach();
                setupNavigation();
            }
        };
        ws.setNullValid(false);
        add(ws);


    }

    private boolean isCurrentWorkspaceValid()
    {
        WorkspaceManager manager = BrixRequestCycle.Locator.getBrix().getWorkspaceManager();
        return currentWorkspace != null && manager.workspaceExists(currentWorkspace.id);
    }

    @Override
    protected void onBeforeRender()
    {
        if (!isCurrentWorkspaceValid())
        {
            List< ? extends WorkspaceEntry> entries = workspaceEntriesModel.getObject();
            if (!entries.isEmpty())
            {
                currentWorkspace = entries.get(0);
            }
            setupNavigation();
        }
        else if (!hasBeenRendered())
        {
            setupNavigation();
        }
        
        super.onBeforeRender();

    }

    private static class WorkspaceEntry implements Serializable
    {
        private String id;
        @SuppressWarnings("unused")
        // used by ChoiceRenderer
        private String visibleName;

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj instanceof WorkspaceEntry == false)
                return false;
            WorkspaceEntry that = (WorkspaceEntry)obj;
            return Objects.equal(id, that.id);
        }
    };

    protected JcrSession getJcrSession()
    {
        return BrixRequestCycle.Locator.getSession(getWorkspace());
    }


    private IModel<List< ? extends WorkspaceEntry>> workspaceEntriesModel = new LoadableDetachableModel<List< ? extends WorkspaceEntry>>()
    {
        @Override
        protected List<WorkspaceEntry> load()
        {
            return getWorkspaces();
        }
    };

    private List<WorkspaceEntry> getWorkspaces()
    {
        Brix brix = BrixRequestCycle.Locator.getBrix();
        List<WorkspaceEntry> workspaces = new ArrayList<WorkspaceEntry>();
        Workspace currentWorkspace = getWorkspace() != null ? brix.getWorkspaceManager()
            .getWorkspace(getWorkspace()) : null;

        for (Plugin p : brix.getPlugins())
        {
            List<Workspace> filtered = brix.filterVisibleWorkspaces(p.getWorkspaces(
                currentWorkspace, false), Context.ADMINISTRATION);
            for (Workspace w : filtered)
            {
                WorkspaceEntry we = new WorkspaceEntry();
                we.id = w.getId();
                we.visibleName = p.getUserVisibleName(w, false);
                workspaces.add(we);
            }
        }

        if (this.currentWorkspace != null && !workspaces.contains(this.currentWorkspace))
        {
            workspaces.add(this.currentWorkspace);
        }
        return workspaces;
    }
}
