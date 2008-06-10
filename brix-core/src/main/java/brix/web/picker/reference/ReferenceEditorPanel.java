package brix.web.picker.reference;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.web.reference.Reference;

public class ReferenceEditorPanel extends FormComponentPanel
{

    public ReferenceEditorPanel(String id)
    {
        super(id);
    }

    public ReferenceEditorPanel(String id, IModel model)
    {
        super(id, model);
    }

    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();
        if (!hasBeenRendered())
        {
            init();
        }
    }

    protected static final String MODAL_WINDOW_ID = "modalWindow";

    @Override
    public void updateModel()
    {
        // don't you dare!
    }

    private void init()
    {
        add(newModalWindow(MODAL_WINDOW_ID));
        final Label label = new Label("label", newLabelModel());
        label.setOutputMarkupId(true);
        add(label);

        add(new IndicatingAjaxLink("edit")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                getModalWindow().setModel(ReferenceEditorPanel.this.getModel());
                getModalWindow().setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
                {
                    public void onClose(AjaxRequestTarget target)
                    {
                        target.addComponent(label);
                        ReferenceEditorPanel.this.onClose(target);
                    }
                });
                getModalWindow().show(target);
            }
        });
    }

    protected void onClose(AjaxRequestTarget target)
    {

    }

    protected ReferenceEditorModalWindow getModalWindow()
    {
        return (ReferenceEditorModalWindow)get(MODAL_WINDOW_ID);
    }

    protected IModel newLabelModel()
    {
        return new PropertyModel(getModel(), "generateUrl");
    }

    private ReferenceEditorConfiguration configuration;

    public ReferenceEditorPanel setConfiguration(ReferenceEditorConfiguration configuration)
    {
        this.configuration = configuration;
        return this;
    }

    public ReferenceEditorConfiguration getConfiguration()
    {
        return configuration;
    }

    protected Component newModalWindow(String id)
    {
        return new ReferenceEditorModalWindow(id, getModel(), getConfiguration());
    }

    @Override
    public boolean checkRequired()
    {
        if (isRequired())
        {
            Reference ref = (Reference)getModelObject();
            if (ref == null || ref.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isInputNullable()
    {
        return false;
    }
}
