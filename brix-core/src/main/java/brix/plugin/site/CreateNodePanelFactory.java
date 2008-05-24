package brix.plugin.site;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.registry.ExtensionPoint;
import brix.web.admin.navigation.NavigationAwarePanel;

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
	
	NavigationAwarePanel<?> newCreateNodePanel(String id, IModel<JcrNode> parentNode);
	
}
