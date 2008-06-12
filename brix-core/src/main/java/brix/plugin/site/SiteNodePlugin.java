package brix.plugin.site;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;

import brix.jcr.wrapper.BrixNode;
import brix.registry.ExtensionPoint;

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

    IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters);

    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode);
    
    Panel<?> newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack);

    NodeConverter getConverterForNode(BrixNode node);
}
