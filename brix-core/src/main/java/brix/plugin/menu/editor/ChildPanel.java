package brix.plugin.menu.editor;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.plugin.menu.ManageMenuPanel;
import brix.plugin.menu.Menu.ChildEntry;
import brix.web.ContainerFeedbackPanel;
import brix.web.picker.reference.ReferenceEditorConfiguration;
import brix.web.picker.reference.ReferenceEditorPanel;
import brix.web.reference.Reference;

public class ChildPanel extends Panel<ChildEntry>
{

    public ChildPanel(String id, IModel<ChildEntry> model)
    {
        super(id, model);

    }

    @Override
    protected void onBeforeRender()
    {
        if (!hasBeenRendered())
        {
            init();
        }
        super.onBeforeRender();
    }

    private void init()
    {
        Form<ChildEntry> form = new Form<ChildEntry>("form", new CompoundPropertyModel<ChildEntry>(
            getModel()));
        add(form);

        form.add(new TextField<String>("title").setRequired(true));

        Reference old = getModelObject().getReference();
        reference = old != null ? new Reference(old) : new Reference();

        ReferenceEditorConfiguration conf = new ReferenceEditorConfiguration();

        ManageMenuPanel panel = findParent(ManageMenuPanel.class);
        conf.setWorkspaceName(panel.getModelObject().getId());

        form.add(new ReferenceEditorPanel("referenceEditor", new PropertyModel<Reference>(this,
            "reference")).setConfiguration(conf));

        form.add(new TextField<String>("cssClass"));

        final Component< ? > feedback;

        add(feedback = new ContainerFeedbackPanel("feedback", form));
        feedback.setOutputMarkupId(true);

        form.add(new IndicatingAjaxButton<Object>("update")
        {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form< ? > form)
            {
                target.addComponent(feedback);
                ChildPanel.this.getModelObject().setReference(new Reference(reference));
                onUpdate();
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form< ? > form)
            {
                target.addComponent(feedback);
            }
        });
    }

    private Reference reference;

    protected void onUpdate()
    {

    }

    @Override
    protected void onDetach()
    {
        super.onDetach();
        if (reference != null)
        {
            reference.detach();
        }
    }

}
