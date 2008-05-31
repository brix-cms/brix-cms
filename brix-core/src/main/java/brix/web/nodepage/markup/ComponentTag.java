package brix.web.nodepage.markup;

import java.util.UUID;

import org.apache.wicket.Component;

public interface ComponentTag extends Tag
{
	/**
	 * The UUID is required to keep track of components on page. It is used to
	 * remove components that no longer have their items present in markup
	 * stream and add components which items are new in the markup stream.
	 * 
	 * @return
	 */
	public UUID getUUID();
	
	public Component<?> getComponent(String id);
}
