package brix.plugin.site.admin;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;

public abstract class RenamePanel extends Panel<BrixNode>
{

    public RenamePanel(String id, IModel<BrixNode> model)
    {
        super(id, model);

        Form< ? > form = new Form<Object>("form");

        newName = model.getObject().getName();
        
        TextField<String> newName = new TextField<String>("newName", new PropertyModel<String>(
            this, "newName"));
        newName.setRequired(true);
        newName.add(new NewNameValidator());
        form.add(newName);

        form.add(new Button<Object>("rename")
        {
            @Override
            public void onSubmit()
            {
                JcrNode node = RenamePanel.this.getModelObject();
                
                if (RenamePanel.this.newName.equals(node.getName()) == false) {
                
                    node.getSession().move(node.getPath(),
                        node.getParent().getPath() + "/" + RenamePanel.this.newName);
                    node.getSession().save();
                }
                onLeave();
            }
        });

        form.add(new Button<Object>("cancel")
        {
            @Override
            public void onSubmit()
            {
                onLeave();
            }
        }.setDefaultFormProcessing(false));

        form.add(new FeedbackPanel("feedback"));

        add(form);
    }

    private class NewNameValidator implements IValidator
    {

        public void validate(IValidatable validatable)
        {
            String name = (String)validatable.getValue();
            
            if (getModelObject().getName().equals(name) == false)
            {
            
                JcrNode parent = getModelObject().getParent();
                if (parent.hasNode(name))
                {
                    validatable.error(new ValidationError().addMessageKey("NewNameValidator")
                        .setVariable("name", name));
                }
            }
        }

    };

    private String newName;

    protected abstract void onLeave();

}
