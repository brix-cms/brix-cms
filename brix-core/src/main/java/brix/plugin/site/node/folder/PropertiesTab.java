package brix.plugin.site.node.folder;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.web.ContainerFeedbackPanel;
import brix.web.model.ModelBuffer;
import brix.web.picker.reference.ReferenceEditorConfiguration;
import brix.web.picker.reference.ReferenceEditorPanel;

public class PropertiesTab extends Panel
{

    public PropertiesTab(String id, final IModel<JcrNode> folderNodeModel)
    {
        super(id, folderNodeModel);

        final ModelBuffer buffer = new ModelBuffer(folderNodeModel);

        Form form = new Form("form")
        {
            @Override
            protected void onSubmit()
            {
                buffer.apply();
                folderNodeModel.getObject().save();
            }
        };
        add(form);

        add(new ContainerFeedbackPanel("feedback", this));

        ReferenceEditorConfiguration conf = new ReferenceEditorConfiguration();

        conf.setDisplayFiles(true);
        conf.setWorkspaceName(folderNodeModel);

        form.add(new ReferenceEditorPanel("redirectReference", buffer
                .forProperty("redirectReference")).setConfiguration(conf));

    }
}
