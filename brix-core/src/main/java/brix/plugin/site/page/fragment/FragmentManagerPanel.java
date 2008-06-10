package brix.plugin.site.page.fragment;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

public class FragmentManagerPanel extends Panel<BrixNode>
{
    public FragmentManagerPanel(String id, IModel<BrixNode> fragmentsNode)
    {
        super(id, fragmentsNode);
        add(new TilesPanel("tiles", fragmentsNode));

    }

}
