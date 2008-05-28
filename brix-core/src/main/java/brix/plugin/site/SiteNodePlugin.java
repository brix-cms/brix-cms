package brix.plugin.site;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;

import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;
import brix.web.admin.navigation.NavigationAwarePanel;

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

    String getNodeType();

    String getName();

    NavigationAwarePanel newManageNodePanel(String id, IModel<BrixNode> nodeModel);

    IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters);

    NavigationAwarePanel newCreateNodePanel(String id, IModel<BrixNode> parentNode);

    NodeConverter getConverterForNode(BrixNode node);
}
