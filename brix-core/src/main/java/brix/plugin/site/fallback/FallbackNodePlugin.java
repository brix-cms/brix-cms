package brix.plugin.site.fallback;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.EmptyRequestTarget;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.NodeConverter;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SiteNodePlugin;

public class FallbackNodePlugin implements SiteNodePlugin
{

    public NodeConverter getConverterForNode(BrixNode node)
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


    public Panel<?> newCreateNodePanel(String id, IModel<BrixNode> parentNode, SimpleCallback goBack)
    {
        return new EmptyPanel(id);
    }

    public IRequestTarget respond(IModel<BrixNode> nodeModel, RequestParameters requestParameters)
    {
        return EmptyRequestTarget.getInstance();
    }

    public IModel<String> newCreateNodeCaptionModel(IModel<BrixNode> parentNode)
    {
    	return null;
    }
    
    private class EmptyPanel extends Panel<Void>
    {

        public EmptyPanel(String id)
        {
            super(id);
        }

    };
}
