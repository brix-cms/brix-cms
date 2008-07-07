package brix.web.tab;

import java.util.List;

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;

public class BrixAjaxTabbedPanel extends AjaxTabbedPanel
{

	public BrixAjaxTabbedPanel(String id, List<IBrixTab> tabs)
	{
		super(id, BrixTabbedPanel.sort(tabs));	
	}

	@Override
	protected String getTabContainerCssClass()
	{
		return "brix-tab-row";
	}
}
