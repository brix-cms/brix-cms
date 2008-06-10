package brix.plugin.site;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;

public interface CreateNodePanelFactory {

	public static ExtensionPoint<CreateNodePanelFactory> POINT = new ExtensionPoint<CreateNodePanelFactory>()
	{
		public Multiplicity getMultiplicity() {
			return Multiplicity.COLLECTION;
		}
		public String getUuid() {
			return CreateNodePanelFactory.class.getName();
		}
	};
	
	public boolean canHandle(String nodeType, JcrNode parent);
	
	Panel<?> newCreateNodePanel(String id, IModel<BrixNode> parentNode);
	
}
