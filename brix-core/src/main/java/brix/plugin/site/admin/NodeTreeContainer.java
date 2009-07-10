package brix.plugin.site.admin;

import org.apache.wicket.markup.html.WebMarkupContainer;

import brix.jcr.wrapper.BrixNode;

/**
 * Allows nested or related components to affect changes to the node tree contained and managed by
 * the component that implements this interface.
 * 
 * This interface must be implemented by a subclass of {@link WebMarkupContainer}
 * 
 * @author igor.vaynberg
 */
public interface NodeTreeContainer
{
    /**
     * Called when the tree selection needs to be changed to the specified node
     * 
     * @param node
     */
    public abstract void selectNode(BrixNode node);

    /**
     * Called when the tree needs to be updated - eg a new node has been inserted
     */
    public abstract void updateTree();

}