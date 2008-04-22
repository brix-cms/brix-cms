package brix.plugin.site.admin;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.BrixRequestCycle.Locator;
import brix.jcr.api.JcrNode;
import brix.web.admin.navigation.NavigationAwarePanel;

public class NodeManagerPanel extends NavigationAwarePanel<JcrNode>
{

    public NodeManagerPanel(String id, IModel<JcrNode> nodeModel)
    {
        super(id, nodeModel);
    }

    protected Brix getBrix()
    {
        return Locator.getBrix();
    }

    public final JcrNode getNode()
    {
        return (JcrNode)getModelObject();
    }

    protected final IModel<JcrNode> getNodeModel()
    {
        return (IModel<JcrNode>)getModel();
    }

}
