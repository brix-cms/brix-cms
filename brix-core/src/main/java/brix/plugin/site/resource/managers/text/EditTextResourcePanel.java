package brix.plugin.site.resource.managers.text;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;
import brix.web.model.ModelBuffer;

public abstract class EditTextResourcePanel extends BrixGenericPanel<BrixNode>
{
	public EditTextResourcePanel(String id, IModel<BrixNode> node)
	{
		super(id, node);

		add(new FeedbackPanel("feedback"));

		Form<?> form = new Form<Void>("form");
		add(form);

		final ModelBuffer model = new ModelBuffer(node);

		form.add(new TextResourceEditor("editor", model));

		form.add(new SubmitLink("save")
		{
			@Override
			public void onSubmit()
			{
				model.apply();
				getNode().save();
				// done
				getSession().info(getString("saved"));
				done();
			}
		});

		form.add(new Link<Void>("cancel")
		{
			@Override
			public void onClick()
			{
				getSession().info(getString("cancelled"));
				done();
			}
		});
	}

	protected abstract void done();

	protected BrixNode getNode()
	{
		return getModelObject();
	}
}
