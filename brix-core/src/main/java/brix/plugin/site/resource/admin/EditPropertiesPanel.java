package brix.plugin.site.resource.admin;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.jcr.wrapper.BrixNode.Protocol;
import brix.web.generic.BrixGenericPanel;
import brix.web.model.ModelBuffer;

public abstract class EditPropertiesPanel extends BrixGenericPanel<BrixNode>
{
	public EditPropertiesPanel(String id, final IModel<BrixNode> nodeModel)
	{
		super(id, nodeModel);

		List<Protocol> protocols = Arrays.asList(Protocol.values());

		final ModelBuffer model = new ModelBuffer(nodeModel);
		Form<?> form = new Form<Void>("form");

		IChoiceRenderer<Protocol> renderer = new IChoiceRenderer<Protocol>()
		{
			public Object getDisplayValue(Protocol object)
			{
				return getString(object.toString());
			}

			public String getIdValue(Protocol object, int index)
			{
				return object.toString();
			}
		};
		IModel<Protocol> protocolModel = model.forProperty("requiredProtocol");
		form.add(new DropDownChoice<Protocol>("requiredProtocol", protocolModel, protocols, renderer)
				.setNullValid(false));

		form.add(new SubmitLink("save")
		{
			@Override
			public void onSubmit()
			{
				BrixNode node = nodeModel.getObject();
				model.apply();
				node.save();
				getSession().info(getString("propertiesSaved"));
				goBack();
			}
		});

		form.add(new Link<Void>("cancel")
		{
			@Override
			public void onClick()
			{
				getSession().info(getString("editingCanceled"));
				goBack();
			}
		});

		add(form);
	}

	abstract void goBack();

}
