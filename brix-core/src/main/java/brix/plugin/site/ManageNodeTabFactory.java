package brix.plugin.site;

import java.util.List;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;
import brix.web.tab.IBrixTab;

/**
 * Factory for creating site node management tabs.
 * 
 * @author Matej Knopp
 */
public interface ManageNodeTabFactory
{

	public static final ExtensionPoint<ManageNodeTabFactory> POINT = new ExtensionPoint<ManageNodeTabFactory>()
	{
		public brix.registry.ExtensionPoint.Multiplicity getMultiplicity()
		{
			return Multiplicity.COLLECTION;
		}

		public String getUuid()
		{
			return ManageNodeTabFactory.class.getName();
		}
	};

	/**
	 * Returns list of node management tabs for given node. 
	 * 
	 * @param nodeModel
	 * @return
	 */
	public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel);

}
