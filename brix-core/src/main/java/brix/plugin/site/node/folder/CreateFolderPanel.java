package brix.plugin.site.node.folder;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import brix.Path;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SiteNavigationTreeNode;
import brix.web.ContainerFeedbackPanel;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.web.util.validators.NodeNameValidator;

public class CreateFolderPanel extends NavigationAwarePanel<BrixNode>
{
    private String name;

    public CreateFolderPanel(String id, IModel<BrixNode> model)
    {
        super(id, model);

        add(new ContainerFeedbackPanel("feedback", this));

        Form form = new Form("form", new CompoundPropertyModel(this))
        {
            protected void onSubmit()
            {
                createFolder();
            }
        };
        add(form);

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
            
            NavigationTreeNode treeNode = new SiteNavigationTreeNode(node);            
            getNavigation().nodeInserted(treeNode);
            getNavigation().selectNode(treeNode);                        
        }
    }

}
