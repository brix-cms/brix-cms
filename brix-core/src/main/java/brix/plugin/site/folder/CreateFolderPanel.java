package brix.plugin.site.folder;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import brix.Path;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SitePlugin;
import brix.web.ContainerFeedbackPanel;
import brix.web.util.validators.NodeNameValidator;

public class CreateFolderPanel extends Panel<BrixNode>
{
    private String name;

    public CreateFolderPanel(String id, IModel<BrixNode> model, final SimpleCallback goBack)
    {
        super(id, model);

        add(new ContainerFeedbackPanel("feedback", this));

        Form<Void> form = new Form<Void>("form", new CompoundPropertyModel(this))
        {
            
        };
        add(form);
        
        form.add(new Button<Void>("create") {
        	@Override
        	public void onSubmit()
        	{
        		createFolder();                
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

    private void createFolder()
    {
        final JcrNode parent = (JcrNode)getModelObject();


        final Path path = new Path(parent.getPath());
        final Path newPath = path.append(new Path(name));

        final JcrSession session = parent.getSession();

        if (session.itemExists(newPath.toString()))
        {
            error("Resource at " + newPath + " already exists");
        }
        else
        {
            FolderNode node = (FolderNode)parent.addNode(name, "nt:folder");
            parent.save();
            
            SitePlugin.get().selectNode(this, node);     
            SitePlugin.get().refreshNavigationTree(this);
        }
    }

}
