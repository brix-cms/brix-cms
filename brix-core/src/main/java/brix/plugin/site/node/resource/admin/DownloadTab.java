package brix.plugin.site.node.resource.admin;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.node.resource.ResourceRequestTarget;

public class DownloadTab extends Panel<BrixNode>
{

    public DownloadTab(String id, final IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        add(new Link("download")
        {
            @Override
            public void onClick()
            {
                getRequestCycle().setRequestTarget(new ResourceRequestTarget(nodeModel, true));
            }
        });
    }

}
