package brix.plugin.site.node.resource.admin;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.node.resource.ResourceRequestTarget;

public class DownloadTab extends Panel<JcrNode>
{

    public DownloadTab(String id, final IModel<JcrNode> nodeModel)
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
