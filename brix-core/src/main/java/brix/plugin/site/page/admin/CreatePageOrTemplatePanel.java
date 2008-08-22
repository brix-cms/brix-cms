package brix.plugin.site.page.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.page.AbstractContainer;
import brix.plugin.site.page.PageNode;
import brix.plugin.site.page.PageSiteNodePlugin;
import brix.plugin.site.page.TemplateNode;
import brix.web.ContainerFeedbackPanel;
import brix.web.util.validators.NodeNameValidator;

public class CreatePageOrTemplatePanel extends NodeManagerPanel
{

	private String name;

	public CreatePageOrTemplatePanel(String id, IModel<BrixNode> containerNodeModel, final String type,
			final SimpleCallback goBack)
	{
		super(id, containerNodeModel);

		String typeName = SitePlugin.get().getNodePluginForType(type).getName();
		add(new Label("typeName", typeName));
	
		Form<?> form = new Form<CreatePageOrTemplatePanel>("form",
				new CompoundPropertyModel<CreatePageOrTemplatePanel>(this));
		add(form);

		form.add(new ContainerFeedbackPanel("feedback", this));
		
		form.add(new SubmitLink("create")
		{
			@Override
			public void onSubmit()
			{
				createPage(type);
			}
		});

		form.add(new Link<Void>("cancel")
		{
			@Override
			public void onClick()
			{
				goBack.execute();
			}
		});

		final TextField<String> tf;
		form.add(tf = new TextField<String>("name"));
		tf.setRequired(true);
		tf.add(NodeNameValidator.getInstance());

	}

	private void createPage(String type)
	{
		final JcrNode parent = getModelObject();

		if (parent.hasNode(name))
		{
			String error = getString("resourceExists", new Model<CreatePageOrTemplatePanel>(
					CreatePageOrTemplatePanel.this));
			error(error);
		}
		else
		{
			JcrNode page = parent.addNode(name, "nt:file");

			AbstractContainer node;

			if (type.equals(PageSiteNodePlugin.TYPE))
			{
				node = PageNode.initialize(page);
			}
			else
			{
				node = TemplateNode.initialize(page);
			}
			
			node.setTitle(name);

			node.setData("");
			name = null;

			parent.save();

			SitePlugin.get().selectNode(this, node, true);
		}
	}

}
