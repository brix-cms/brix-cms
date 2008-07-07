package brix.plugin.site.picker.reference;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.web.generic.BrixGenericPanel;
import brix.web.reference.Reference;

public class ReferenceEditorWithButtons extends BrixGenericPanel<Reference>
{

    public ReferenceEditorWithButtons(String id, ReferenceEditorConfiguration configuration)
    {
        super(id);
        init(configuration);
    }

    public ReferenceEditorWithButtons(String id, IModel<Reference> model,
            ReferenceEditorConfiguration configuration)
    {
        super(id, model);
        init(configuration);
    }

    private Reference reference;

    private void init(ReferenceEditorConfiguration configuration)
    {
        Reference old = getModelObject();

        reference = old != null ? new Reference(old) : new Reference();

        add(new ReferenceEditor("editor", new PropertyModel<Reference>(this, "reference"), configuration));

        add(new AjaxLink<Void>("ok")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onOk(target);
            }
        });

        add(new AjaxLink<Void>("cancel")
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
