package brix.web.picker.reference;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.web.picker.node.NodePicker;
import brix.web.reference.Reference;

public abstract class NodeUrlTab extends Panel
{

    public NodeUrlTab(String id, IModel /* Reference */model)
    {
        super(id, model);

        setOutputMarkupId(true);

        List<Reference.Type> choices = Arrays.asList(Reference.Type.values());
        DropDownChoice choice;
        add(choice = new DropDownChoice("type", new PropertyModel(this.getModel(), "type"), choices)
        {
            @Override
            public boolean isVisible()
            {
                return getConfiguration().isAllowNodePicker() &&
                        getConfiguration().isAllowURLEdit();
            }
        });

        choice.add(new AjaxFormComponentUpdatingBehavior("onchange")
        {
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                target.addComponent(NodeUrlTab.this);
            }
        });

        add(new NodePicker("nodePicker", getReference().getNodeModel(), getConfiguration()
                .getWorkspaceName(), getConfiguration().getNodeFilter())
        {
            @Override
            public boolean isDisplayFiles()
            {
                return getConfiguration().isDisplayFiles();
            }

            @Override
            public boolean isVisible()
            {
                return getConfiguration().isAllowNodePicker() &&
                        getReference().getType() == Reference.Type.NODE;
            }
        });

        add(new UrlPanel("urlPanel", new PropertyModel(getModel(), "url"))
        {
            @Override
            public boolean isVisible()
            {
                return getConfiguration().isAllowURLEdit() &&
                        getReference().getType() == Reference.Type.URL;
            }
        });


    }

    private Reference getReference()
    {
        return (Reference)getModelObject();
    }

    protected abstract ReferenceEditorConfiguration getConfiguration();

}
