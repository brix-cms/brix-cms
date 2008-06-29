package brix.markup.tag;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

/**
 * HTML tag that can have a wicket component attached to it.
 * 
 * @author Matej Knopp
 */
public interface ComponentTag extends Tag
{
	/**
	 * The unique identifier is required to keep track of components on page. It
	 * is used to remove components that no longer have their items present in
	 * markup stream and add components which items are new in the markup
	 * stream.
	 * 
	 * The identifier must be unique for every tag instance and must not change
	 * during the life of that instance.
	 * 
	 * @return
	 */
	public String getUniqueTagId();

	/**
	 * Creates wicket component for this tag. If there is no component returns
	 * null.
	 * 
	 * @param id
	 *            component id (will be generated and based on
	 *            {@link #getUniqueTagId()} result)
	 * @param pageNodeModel
	 * 			  model to JcrNode that represents the target page            
	 * @return
	 */
	public Component getComponent(String id, IModel<BrixNode> pageNodeModel);
}
