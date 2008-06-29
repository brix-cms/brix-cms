package brix.plugin.site.admin;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class NodeManagerTabbedPanel extends Panel
{
	public NodeManagerTabbedPanel(String id, List<ITab> tabs)
	{
		super(id);
		
		add(new TabbedPanel("tabbedPanel", tabs) {
			@Override
			protected String getTabContainerCssClass()
			{				
				return "brix-site-manager-tab-row";
			}
		});
	}

}
