package brix.web.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Objects;

import brix.Brix;
import brix.Path;
import brix.Plugin;
import brix.auth.Action.Context;
import brix.jcr.api.JcrSession;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class AdminPanel extends Panel<Void> 
{

    private TabbedPanel tabbedPanel;

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
            setupTabbedPanel();
        }
    }

    public void setWorkspace(String workspace)
    {
        setWorkspace(workspace, null);
    }

    
    private Brix getBrix()
    {
    	return Brix.get();
    }


    public AdminPanel(String id, String workspace, Path root)
    {
        super(id);
        
        add(HeaderContributor.forCss(CSS));

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
                setupTabbedPanel();
            }
        };
        ws.setNullValid(false);
        add(ws);


    }
    
    private void setupTabbedPanel()
    {    	
    	if (tabbedPanel != null)
    	{
    		tabbedPanel.remove();
    	}
    	
    	List<ITab> tabs = new ArrayList<ITab>();
    	if (currentWorkspace != null)
    	{
    		Brix brix = Brix.get();
    		Workspace workspace = brix.getWorkspaceManager().getWorkspace(currentWorkspace.id);
    		for (Plugin p : brix.getPlugins())
    		{
    			ITab tab = p.newTab(workspace);
    			if (tab != null)
    			{
    				tabs.add(tab);
    			}
    		}
    	}
    	
    	tabbedPanel = new TabbedPanel("tabbedPanel", tabs) 
    	{
    		@Override
    		protected String getTabContainerCssClass()
    		{
    			return "brix-plugins-tabbed-panel-row";
    		}
    	};
    	add(tabbedPanel);
    }

    private boolean isCurrentWorkspaceValid()
    {
        WorkspaceManager manager = getBrix().getWorkspaceManager();
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
            setupTabbedPanel();
        }
        else if (!hasBeenRendered())
        {
        	setupTabbedPanel();
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
        return getBrix().getCurrentSession(getWorkspace());
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
        Brix brix = getBrix();
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
    
    private static final ResourceReference CSS = new CompressedResourceReference(AdminPanel.class, "res/style.css");
}
