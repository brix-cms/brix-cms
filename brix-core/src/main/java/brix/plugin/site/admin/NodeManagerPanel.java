package brix.plugin.site.admin;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;

public class NodeManagerPanel extends BrixGenericPanel<BrixNode>
{

    public NodeManagerPanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);
    }
}
