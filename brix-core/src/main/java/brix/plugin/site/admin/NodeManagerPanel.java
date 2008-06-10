package brix.plugin.site.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

public class NodeManagerPanel extends Panel<BrixNode>
{

    public NodeManagerPanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);
    }

    public final BrixNode getNode()
    {
        return getModelObject();
    }
}
