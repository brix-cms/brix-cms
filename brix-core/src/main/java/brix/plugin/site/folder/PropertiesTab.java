package brix.plugin.site.folder;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.ContainerFeedbackPanel;
import brix.web.generic.BrixGenericPanel;
import brix.web.model.ModelBuffer;
import brix.web.picker.reference.ReferenceEditorConfiguration;
import brix.web.picker.reference.ReferenceEditorPanel;
import brix.web.reference.Reference;

public class PropertiesTab extends BrixGenericPanel<BrixNode>
{

	public PropertiesTab(String id, final IModel<BrixNode> folderNodeModel)
	{
		super(id, folderNodeModel);

		final ModelBuffer buffer = new ModelBuffer(folderNodeModel);

		Form<Void> form = new Form<Void>("form");

		form.add(new SubmitLink("submit")
		{
			@Override
			public void onSubmit()
			{
				buffer.apply();
				folderNodeModel.getObject().save();
				getSession().info(PropertiesTab.this.getString("propertiesSaved"));
			}
		});
		add(form);

		add(new ContainerFeedbackPanel("feedback", this));

		ReferenceEditorConfiguration conf = new ReferenceEditorConfiguration();

		conf.setDisplayFiles(true);
		conf.setWorkspaceName(folderNodeModel);

		IModel<Reference> model = buffer.forProperty("redirectReference");
		form.add(new ReferenceEditorPanel("redirectReference", model).setConfiguration(conf));

	}
}
