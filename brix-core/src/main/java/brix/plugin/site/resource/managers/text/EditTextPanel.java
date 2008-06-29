package brix.plugin.site.resource.managers.text;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;

public abstract class EditTextPanel extends BrixGenericPanel<BrixNode>
{

	public EditTextPanel(String id, IModel<BrixNode> model)
	{
		super(id, model);

		Form<Void> form = new Form<Void>("form");
		add(form);

		content = getFileNode().getDataAsString();

		form.add(new TextArea<String>("text", new PropertyModel<String>(this, "content")));

		form.add(new SubmitLink("save")
		{
			@Override
			public void onSubmit()
			{
				BrixFileNode node = getFileNode();
				node.setData(content);
				node.save();
				getSession().info(getString("textSaved"));
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

	}

	private String content;

	private BrixFileNode getFileNode()
	{
		return (BrixFileNode) getModelObject();
	}

	protected abstract void goBack();

}
