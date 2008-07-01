package brix.plugin.site;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;

import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;

/**
 * Plugin that handles node of certain type. This is not a global plugin, the
 * scope if this plugin is {@link SitePlugin}. Main purpose of
 * {@link SiteNodePlugin} is to respond when an URL for site node is requested.
 * 
 * @author Matej Knopp
 */
public interface SiteNodePlugin
{
	public static ExtensionPoint<SiteNodePlugin> POINT = new ExtensionPoint<SiteNodePlugin>()
	{
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity()
		{
			return Multiplicity.COLLECTION;
		}

		public String getUuid()
		{
			return SiteNodePlugin.class.getName();
		}
	};

	/**
	 * Returns the node type of nodes that this plugin can handle.
	 * 
	 * @see BrixNode#setNodeType(String)
	 * @return
	 */
	String getNodeType();

	/**
	 * Returns the user readable name of this plugin.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Returns the request target if this plugin is capable of creating a
	 * response for the node. Otherwise returns <code>null</code>
	 * 
	 * @param nodeModel
	 * @param requestParameters
	 * @return
	 */
	IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters);

	/**
	 * Returns model caption of Create link for this plugin.
	 * 
	 * @param parentNode
	 * @return
	 */
	public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode);

	/**
	 * Returns an instance of panel that should create node of type this plugin
	 * can handle.
	 * 
	 * @param id
	 *            panel component id
	 * @param parentNode
	 *            parent node of the new node
	 * @param goBack
	 *            simple callback that should be invoked after node creation or
	 *            on cancel
	 * @return panel instance
	 */
	Panel newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack);

	/**
	 * This method returns converter that is capable to convert the given node
	 * to a node this plugin can handle, or <code>null</code> if such
	 * converter does not exist.
	 * 
	 * @param node
	 * @return
	 */
	NodeConverter getConverterForNode(BrixNode node);
}
