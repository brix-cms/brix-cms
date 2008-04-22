package brix.web.picker.reference;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.web.reference.Reference;

public class ReferenceEditorWithButtons extends Panel
{

    public ReferenceEditorWithButtons(String id, ReferenceEditorConfiguration configuration)
    {
        super(id);
        init(configuration);
    }

    public ReferenceEditorWithButtons(String id, IModel model,
            ReferenceEditorConfiguration configuration)
    {
        super(id, model);
        init(configuration);
    }

    private Reference reference;

    private void init(ReferenceEditorConfiguration configuration)
    {
        Reference old = ((Reference)getModelObject());

        reference = old != null ? new Reference(old) : new Reference();

        add(new ReferenceEditor("editor", new PropertyModel(this, "reference"), configuration));

        add(new AjaxLink("ok")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onOk(target);
            }
        });

        add(new AjaxLink("cancel")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onCancel(target);
            }
        });
    }

    protected Reference getEditedReference()
    {
        return reference;
    }

    protected void onCancel(AjaxRequestTarget target)
    {

    }

    protected void onOk(AjaxRequestTarget target)
    {
        setModelObject(getEditedReference());
    }

}
