package brix.plugin.site.resource.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.Streams;

import brix.Brix;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SimpleCallback;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.web.ContainerFeedbackPanel;

public class UploadResourcesPanel extends NodeManagerPanel
{

    private Collection<FileUpload> uploads = new ArrayList<FileUpload>();
    private boolean overwrite = false;

    public UploadResourcesPanel(String id, IModel<BrixNode> model, final SimpleCallback goBack)
    {
        super(id, model);
        add(new ContainerFeedbackPanel("feedback", this));

        Form<Void> form = new Form<Void>("form", new CompoundPropertyModel(this))
        {
            protected void onSubmit()
            {
                processUploads();
            }
        };
        add(form);
        
        form.add(new Link<Void>("cancel") {
        	@Override
        	public void onClick()
        	{
        		goBack.execute();
        	}
        });

        form.add(new MultiFileUploadField("uploads"));
        form.add(new CheckBox("overwrite"));
    }

    private void processUploads()
    {
        final BrixNode parentNode = getNode();

        for (FileUpload upload : uploads)
        {

            final String fileName = upload.getClientFileName();

            if (parentNode.hasNode(fileName))
            {
                if (overwrite)
                {
                    parentNode.getNode(fileName).remove();
                }
                else
                {
                    getSession().error("File " + upload.getClientFileName() + " already exists");
                    continue;
                }
            }

            BrixNode newNode = (BrixNode) parentNode.addNode(fileName, "nt:file");

            try
            {
                // copy the upload into a temp file and assign that
                // output stream to the node
                File temp = File.createTempFile(
                        Brix.NS + "-upload-" + UUID.randomUUID().toString(), null);

                Streams.copy(upload.getInputStream(), new FileOutputStream(temp));
                upload.closeStreams();

                String mime = upload.getContentType();

                BrixFileNode file = BrixFileNode.initialize(newNode, mime);
                file.setData(new FileInputStream(temp));
                file.getParent().save();

            }
            catch (IOException e)
            {
                throw new IllegalStateException(e);
            }
        }

                
        SitePlugin.get().selectNode(this, parentNode, true);
    }

    @Override
    protected void onDetach()
    {
        uploads.clear();
        super.onDetach();
    }
}
