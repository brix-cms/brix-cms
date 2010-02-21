package brix.plugin.site.resource.managers.text;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.jcr.wrapper.BrixNode.Protocol;
import brix.web.model.ModelBuffer;

public class TextResourceEditor extends Panel
{


	private DropDownChoice<Protocol> requiredProtocol;

	public TextResourceEditor(String id, ModelBuffer model)
	{
		super(id);

		// protocol field
		List<Protocol> protocols = Arrays.asList(Protocol.values());
		IChoiceRenderer<Protocol> renderer = new ProtocolRenderer();
		IModel<Protocol> protocolModel = model.forProperty("requiredProtocol");
		requiredProtocol = new DropDownChoice<Protocol>("requiredProtocol", protocolModel,
				protocols, renderer);
		add(requiredProtocol.setNullValid(false));

		// mimetype field
		IModel<String> mimeTypeModel = model.forProperty("mimeType");
		add(new TextField<String>("mimeType", mimeTypeModel).add(new TextMimeTypeValidator())
				.setLabel(new ResourceModel("mimeType")));

		// content field
		IModel<String> contentModel = model.forProperty("dataAsString");
		add(new TextArea<String>("content", contentModel));
	}

	@Override
	protected void onBeforeRender()
	{
		// default require protocol to preserve
		if (requiredProtocol.getModelObject() == null)
		{
			requiredProtocol.setModelObject(Protocol.PRESERVE_CURRENT);
		}

		super.onBeforeRender();
	}

	private final class ProtocolRenderer implements IChoiceRenderer<Protocol>
	{
		public Object getDisplayValue(Protocol object)
		{
			return getString(object.toString());
		}

		public String getIdValue(Protocol object, int index)
		{
			return object.toString();
		}
	}
}
