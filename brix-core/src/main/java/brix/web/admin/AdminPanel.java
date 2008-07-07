package brix.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.Plugin;
import brix.web.generic.BrixGenericPanel;
import brix.web.tab.BrixTabbedPanel;
import brix.web.tab.IBrixTab;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceModel;

public class AdminPanel extends BrixGenericPanel<Workspace> 
{

    private TabbedPanel tabbedPanel;

        protected Panel newWorkspaceSwitcher(String id, IModel<Workspace> workspaceModel)
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
    	
    	List<IBrixTab> tabs = new ArrayList<IBrixTab>();
    	
    	Brix brix = Brix.get();
    	for (Plugin p : brix.getPlugins())
    	{
    		List<IBrixTab> pluginTabs = p.newTabs(getModel());
    		if (tabs != null)
    		{
    			tabs.addAll(pluginTabs);
    		}    		
    	};
    	
    	tabbedPanel = new BrixTabbedPanel("tabbedPanel", tabs) 
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
