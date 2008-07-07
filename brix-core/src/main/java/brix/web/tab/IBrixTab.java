package brix.web.tab;

import org.apache.wicket.extensions.markup.html.tabs.ITab;

/**
 * Special kind of tab that supports tab ordering. Each BrixTab has a priority
 * that affects the order of displayed tabs.
 * 
 * @author Matej Knopp
 * 
 */
public interface IBrixTab extends ITab
{
	/**
	 * Returns the priority of this tab. Tabs with bigger priority go before
	 * tabs with lower priority.
	 * 
	 * @return
	 */
	public int getPriority();
}
