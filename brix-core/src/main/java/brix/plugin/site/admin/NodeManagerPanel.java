package brix.plugin.site.admin;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.web.admin.navigation.NavigationAwarePanel;

public class NodeManagerPanel extends NavigationAwarePanel<BrixNode>
{

    public NodeManagerPanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);
    }

    protected Brix getBrix()
    {
        return Brix.get();
    }

    public final BrixNode getNode()
    {
        return getModelObject();
    }
}
