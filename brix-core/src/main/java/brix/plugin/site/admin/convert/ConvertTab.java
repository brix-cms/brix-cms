package brix.plugin.site.admin.convert;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;

public class ConvertTab extends Panel<BrixNode>
{

    public ConvertTab(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        add(new ConvertNodePanel("convert", nodeModel));
    }

}
