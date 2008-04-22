package brix.plugin.site.node.tilepage.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.admin.ConvertNodePanel;

public class ConvertTab extends Panel<JcrNode>
{

    public ConvertTab(String id, IModel<JcrNode> nodeModel)
    {
        super(id, nodeModel);

        add(new ConvertNodePanel("convert", nodeModel));
    }

}
