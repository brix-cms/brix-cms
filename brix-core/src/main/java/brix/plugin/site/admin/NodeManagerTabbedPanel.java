package brix.plugin.site.admin;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import brix.web.tab.BrixTabbedPanel;
import brix.web.tab.IBrixTab;

public class NodeManagerTabbedPanel extends Panel
{
	public NodeManagerTabbedPanel(String id, List<IBrixTab> tabs)
	{
		super(id);
		
		add(new BrixTabbedPanel("tabbedPanel", tabs) {
			@Override
			protected String getTabContainerCssClass()
			{				
				return "brix-site-manager-tab-row";
			}
		});
	}

}
