package brix.web.tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;

public class BrixTabbedPanel extends TabbedPanel
{

	public BrixTabbedPanel(String id, List<IBrixTab> tabs)
	{
		super(id, sort(tabs));
	}
	
	static List<ITab> sort(List<IBrixTab> tabs)
	{
		List<ITab> result = new ArrayList<ITab>();		
		
		result.addAll(tabs);
		Collections.sort(result, new Comparator<ITab>() {
			public int compare(ITab o1, ITab o2)
			{
				IBrixTab t1 = (IBrixTab) o1;
				IBrixTab t2 = (IBrixTab) o2;
				
				return t2.getPriority() - t1.getPriority();
			}
		});
		
		return result;
	}	
	
	@Override
	protected String getTabContainerCssClass()
	{
		return "brix-tab-row";
	}

}
