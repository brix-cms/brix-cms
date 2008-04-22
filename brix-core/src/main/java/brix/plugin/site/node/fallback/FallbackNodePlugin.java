package brix.plugin.site.node.fallback;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.EmptyRequestTarget;

import brix.jcr.api.JcrNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SiteNodePlugin;
import brix.web.admin.navigation.NavigationAwarePanel;

public class FallbackNodePlugin implements SiteNodePlugin
{

    public NodeConverter getConverterForNode(JcrNode node)
    {
        return null;
    }

    public String getName()
    {
        return "Unknown";
    }

    public String getNodeType()
    {
        return null;
    }


    public NavigationAwarePanel newCreateNodePanel(String id, IModel<JcrNode> parentNode)
    {
        return new EmptyPanel(id);
    }

    public NavigationAwarePanel newManageNodePanel(String id, IModel<JcrNode> nodeModel)
    {
        return new EmptyPanel(id);
    }

    public IRequestTarget respond(IModel<JcrNode> nodeModel, RequestParameters requestParameters)
    {
        return EmptyRequestTarget.getInstance();
    }

    private class EmptyPanel extends NavigationAwarePanel
    {

        public EmptyPanel(String id)
        {
            super(id);
        }

    };
}
