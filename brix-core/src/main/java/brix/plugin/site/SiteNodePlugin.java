package brix.plugin.site;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;

import brix.jcr.api.JcrNode;
import brix.registry.Point;
import brix.web.admin.navigation.NavigationAwarePanel;

public interface SiteNodePlugin
{
    public static Point<SiteNodePlugin> POINT = new Point<SiteNodePlugin>()
    {

        public brix.registry.Point.Multiplicity getMultiplicity()
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

    NavigationAwarePanel newManageNodePanel(String id, IModel<JcrNode> nodeModel);

    IRequestTarget respond(IModel<JcrNode> nodeModel, RequestParameters requestParameters);

    NavigationAwarePanel newCreateNodePanel(String id, IModel<JcrNode> parentNode);

    NodeConverter getConverterForNode(JcrNode node);
}
