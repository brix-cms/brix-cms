package brix.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.Plugin;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

public class AdminPanel extends Panel<Workspace> 
{

    private TabbedPanel tabbedPanel;

        protected Panel<?> newWorkspaceSwitcher(String id, IModel<Workspace> workspaceModel)
    {
    	return new WorkspaceSwitcher(id, workspaceModel);
    }

    public AdminPanel(String id, String workspace)
    {
        super(id);
        
        add(HeaderContributor.forCss(CSS));

        setModel(new WorkspaceModel(workspace));       
    }
    
    private void setupTabbedPanel()
    {    	
    	if (tabbedPanel != null)
    	{
    		tabbedPanel.remove();
    	}
    	
    	List<ITab> tabs = new ArrayList<ITab>();
    	
    	Brix brix = Brix.get();
    	for (Plugin p : brix.getPlugins())
    	{
    		List<ITab> pluginTabs = p.newTabs(getModel());
    		if (tabs != null)
    		{
    			tabs.addAll(pluginTabs);
    		}    		
    	};
    	
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

    @Override
    protected void onBeforeRender()
    {
    	if (!hasBeenRendered())
    	{    		
            add(newWorkspaceSwitcher("switcher", getModel()));
            setupTabbedPanel();
    	}        
        
        super.onBeforeRender();
    }
    
    private static final ResourceReference CSS = new CompressedResourceReference(AdminPanel.class, "res/style.css");
}
