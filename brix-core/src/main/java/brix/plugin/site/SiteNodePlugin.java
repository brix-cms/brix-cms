package brix.plugin.site;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;

import brix.jcr.api.JcrNode;
import brix.web.admin.navigation.NavigationAwarePanel;

public interface SiteNodePlugin
{
    String getNodeType();

    String getName();

    NavigationAwarePanel newManageNodePanel(String id, IModel<JcrNode> nodeModel);

    IRequestTarget respond(IModel<JcrNode> nodeModel, RequestParameters requestParameters);

    NavigationAwarePanel newCreateNodePanel(String id, IModel<JcrNode> parentNode);

    NodeConverter getConverterForNode(JcrNode node);
}
