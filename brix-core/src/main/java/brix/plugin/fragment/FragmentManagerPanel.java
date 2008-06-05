package brix.plugin.fragment;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.admin.navigation.NavigationAwarePanel;

public class FragmentManagerPanel extends NavigationAwarePanel<BrixNode>
{
    public FragmentManagerPanel(String id, IModel<BrixNode> fragmentsNode)
    {
        super(id, fragmentsNode);
        add(new TilesPanel("tiles", fragmentsNode));

    }

}
