package brix.web.tab;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.model.IModel;

public abstract class AbstractBrixTab extends AbstractTab implements IBrixTab
{
	private final int priority;
	
	public AbstractBrixTab(IModel<String> title)
	{
		this(title, 0);
	}
	
	public AbstractBrixTab(IModel<String> title, int priority)
	{
		super(title);
		this.priority = priority;
	}	

	public int getPriority()
	{
		return priority;
	}

}
