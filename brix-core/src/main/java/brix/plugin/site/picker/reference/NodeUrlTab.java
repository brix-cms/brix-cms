package brix.plugin.site.picker.reference;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.plugin.site.picker.node.SiteNodePicker;
import brix.web.generic.BrixGenericPanel;
import brix.web.reference.Reference;
import brix.web.reference.Reference.Type;

public abstract class NodeUrlTab extends BrixGenericPanel<Reference>
{

	public NodeUrlTab(String id, IModel<Reference> model)
	{
		super(id, model);

		setOutputMarkupId(true);

		List<Reference.Type> choices = Arrays.asList(Reference.Type.values());
		DropDownChoice<Reference.Type> choice;

		IChoiceRenderer<Reference.Type> renderer = new IChoiceRenderer<Reference.Type>()
		{
			public Object getDisplayValue(Type object)
			{
				return getString(object.toString());
			}

			public String getIdValue(Type object, int index)
			{
				return object.toString();
			}
		};

		add(choice = new DropDownChoice<Reference.Type>("type", new PropertyModel<Reference.Type>(this.getModel(),
				"type"), choices, renderer)
		{
			@Override
			public boolean isVisible()
			{
				return getConfiguration().isAllowNodePicker() && getConfiguration().isAllowURLEdit();
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

		add(new SiteNodePicker("nodePicker", getReference().getNodeModel(), getConfiguration().getWorkspaceName(),
				getConfiguration().getNodeFilter())
		{
			@Override
			public boolean isVisible()
			{
				return getConfiguration().isAllowNodePicker() && getReference().getType() == Reference.Type.NODE;
			}
		});

		add(new UrlPanel("urlPanel", new PropertyModel<String>(getModel(), "url"))
		{
			@Override
			public boolean isVisible()
			{
				return getConfiguration().isAllowURLEdit() && getReference().getType() == Reference.Type.URL;
			}
		});

	}

	private Reference getReference()
	{
		return (Reference) getModelObject();
	}

	protected abstract ReferenceEditorConfiguration getConfiguration();

}
