package brix.plugin.site.admin.convert;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;

public class ConvertTab extends BrixGenericPanel<BrixNode>
{

    public ConvertTab(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        add(new ConvertNodePanel("convert", nodeModel));
    }

}
