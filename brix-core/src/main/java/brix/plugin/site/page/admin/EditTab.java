/**
 * 
 */
package brix.plugin.site.page.admin;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.codepress.CodePressEnabler;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.page.TemplateSiteNodePlugin;
import brix.plugin.site.picker.node.SiteNodePickerPanel;
import brix.tinymce.TinyMceEnabler;
import brix.web.ContainerFeedbackPanel;
import brix.web.model.ModelBuffer;
import brix.web.picker.node.NodeTypeFilter;
import brix.web.tree.NodeFilter;

abstract class EditTab extends NodeManagerPanel
{

	private boolean codeEditorEnabled = false;
	private boolean wysiwygEditorEnabled = false;

	public EditTab(String id, final IModel<BrixNode> nodeModel)
	{
		super(id, nodeModel);

		Brix brix = getModelObject().getBrix();
		final boolean useCodepress = brix.getConfig().getAdminConfig().isEnableCodePress();
		final boolean useWysiwyg = brix.getConfig().getAdminConfig().isEnableWysiwyg();

		Form<Void> form = new Form<Void>("form");
		add(form);

		final ModelBuffer adapter = new ModelBuffer(nodeModel);
		IModel<String> stringModel = adapter.forProperty("title");

		form.add(new TextField<String>("title", stringModel));

		String workspace = nodeModel.getObject().getSession().getWorkspace().getName();
		NodeFilter filter = new NodeTypeFilter(TemplateSiteNodePlugin.TYPE);

		IModel<BrixNode> model = adapter.forNodeProperty("template");

		form.add(new SiteNodePickerPanel("templatePicker", model, workspace, filter));

		IModel<Boolean> booleanModel = adapter.forProperty("requiresSSL");
		form.add(new CheckBox("requiresSSL", booleanModel));

		stringModel = adapter.forProperty("dataAsString");
		TextArea<String> content = new TextArea<String>("content", stringModel);
		form.add(content);

		if (useCodepress)
		{
			content.add(new CodePressEnabler("html", true)
			{
				@Override
				public boolean isEnabled(Component component)
				{
					return codeEditorEnabled;
				}
			});
		}
		if (useWysiwyg)
		{
			content.add(new TinyMceEnabler()
			{
				@Override
				public boolean isEnabled(Component component)
				{
					return wysiwygEditorEnabled;
				}
			});
		}
		form.add(new ContainerFeedbackPanel("feedback", this));

		form.add(new DisableEditorsButton("disable-editors"));
		form.add(new EnableCodeEditorButton("enable-code-editor").setVisible(useCodepress));
		form.add(new EnableWysiwygEditorButton("enable-wysiwig-editor").setVisibilityAllowed(useWysiwyg));

		form.add(new SubmitLink("save")
		{
			@Override
			public void onSubmit()
			{
				JcrNode node = nodeModel.getObject();
				node.checkout();
				adapter.apply();
				node.save();
				node.checkin();

				getSession().info(getString("status.saved"));
				goBack();
			}
		});

		form.add(new Link<Void>("cancel")
		{

			@Override
			public void onClick()
			{
				getSession().info(getString("status.cancelled"));
				goBack();
			}

		});
	}

	private class EnableCodeEditorButton extends Button
	{
		public EnableCodeEditorButton(String id)
		{
			super(id);
			setDefaultFormProcessing(false);
		}

		@Override
		public void onSubmit()
		{
			codeEditorEnabled = true;
			wysiwygEditorEnabled = false;
		}

		@Override
		public boolean isEnabled()
		{
			return codeEditorEnabled == false;
		}
	}

	private class EnableWysiwygEditorButton extends Button
	{
		public EnableWysiwygEditorButton(String id)
		{
			super(id);
			setDefaultFormProcessing(false);
		}

		@Override
		public void onSubmit()
		{
			codeEditorEnabled = false;
			wysiwygEditorEnabled = true;
		}

		@Override
		public boolean isEnabled()
		{
			return wysiwygEditorEnabled == false;
		}
	}

	private class DisableEditorsButton extends Button
	{
		public DisableEditorsButton(String id)
		{
			super(id);
			setDefaultFormProcessing(false);
		}

		@Override
		public void onSubmit()
		{
			codeEditorEnabled = false;
			wysiwygEditorEnabled = false;
		}

		@Override
		public boolean isEnabled()
		{
			return wysiwygEditorEnabled == true || codeEditorEnabled == true;
		}
	}

	abstract void goBack();

}