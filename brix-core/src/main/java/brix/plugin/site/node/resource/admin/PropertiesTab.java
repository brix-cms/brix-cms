package brix.plugin.site.node.resource.admin;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.BrixNode.Protocol;
import brix.plugin.site.admin.ConvertNodePanel;
import brix.web.model.ModelBuffer;

public class PropertiesTab extends Panel<BrixNode>
{

    public PropertiesTab(String id, final IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        add(new ConvertNodePanel("convert", nodeModel));
        
        List<Protocol> protocols = Arrays.asList(Protocol.values());
        
        final ModelBuffer model = new ModelBuffer(nodeModel);
        Form<?> form = new Form<Void>("form");
        
        form.add(new DropDownChoice<Protocol>("protocol", model.forProperty("requiredProtocol"), protocols).setNullValid(false));

        form.add(new Button<Void>("save") {
        	@Override
        	public void onSubmit() {
        		BrixNode node = nodeModel.getObject();
        		node.checkout();
        		model.apply();
        		node.checkin();
        		node.save();        	
        	}
        });
        
        add(form);
    }

}
