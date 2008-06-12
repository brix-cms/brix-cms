package brix.plugin.site.page.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.page.AbstractContainer;
import brix.plugin.site.page.Page;
import brix.plugin.site.page.PageSiteNodePlugin;
import brix.plugin.site.page.Template;
import brix.web.ContainerFeedbackPanel;
import brix.web.util.validators.NodeNameValidator;

public class CreatePageOrTemplatePanel extends NodeManagerPanel
{

    private String name;

    public CreatePageOrTemplatePanel(String id, IModel<BrixNode> containerNodeModel, final String type, final SimpleCallback goBack)
    {
        super(id, containerNodeModel);


        add(new Label("label", "Create New " + type + ":"));

        add(new ContainerFeedbackPanel("feedback", this));
        Form form = new Form<Void>("form", new CompoundPropertyModel(this));
        add(form);
        
        form.add(new Button<Void>("create") {
        	@Override
        	public void onSubmit()
        	{
        		createPage(type);
        	}
        });
        
        form.add(new Button<Void>("cancel") {
        	@Override
        	public void onSubmit()
        	{
        		goBack.execute();
        	}
        }.setDefaultFormProcessing(false));

        final TextField tf;
        form.add(tf = new TextField("name"));
        tf.setRequired(true);
        tf.add(NodeNameValidator.getInstance());

    }

    private void createPage(String type)
    {
        final JcrNode parent = getNode();

        if (parent.hasNode(name))
        {
            error("Resource with name '" + name + "' already exists");
        }
        else
        {

            JcrNode page = parent.addNode(name, "nt:file");

            AbstractContainer node;

            if (type.equals(PageSiteNodePlugin.TYPE))
            {
                node = Page.initialize(page);
            }
            else
            {
                node = Template.initialize(page);
            }

            node.setData("");
            name = null;
            
            parent.save();

            SitePlugin.get().selectNode(this, node);         
        }
    }

}
