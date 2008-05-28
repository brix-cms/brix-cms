package brix.plugin.site;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;

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
	
	public int getPriority();

	public List<ITab> getManageNodeTabs(IModel<BrixNode> nodeModel);

}
