/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.plugin.site.resource.admin;

import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.io.Streams;
import org.brixcms.Brix;
import org.brixcms.jcr.wrapper.BrixFileNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.web.ContainerFeedbackPanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class UploadResourcesPanel extends NodeManagerPanel {
    private Collection<FileUpload> uploads = new ArrayList<FileUpload>();
    private boolean overwrite = false;

    public UploadResourcesPanel(String id, IModel<BrixNode> model, final SimpleCallback goBack) {
        super(id, model);

        Form<?> form = new Form<UploadResourcesPanel>("form", new CompoundPropertyModel<UploadResourcesPanel>(this));
        add(form);

        form.add(new ContainerFeedbackPanel("feedback", this));

        form.add(new SubmitLink("upload") {
            @Override
            public void onSubmit() {
                processUploads();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                goBack.execute();
            }
        });

        form.add(new MultiFileUploadField("uploads"));
        form.add(new CheckBox("overwrite"));
    }

    private void processUploads() {
        final BrixNode parentNode = getModelObject();

        for (final FileUpload upload : uploads) {
            final String fileName = upload.getClientFileName();

            if (parentNode.hasNode(fileName)) {
                if (overwrite) {
                    parentNode.getNode(fileName).remove();
                } else {
                    class ModelObject implements Serializable {
                        @SuppressWarnings("unused")
                        private String fileName = upload.getClientFileName();
                    }

                    getSession().error(getString("fileExists", new Model<ModelObject>(new ModelObject())));
                    continue;
                }
            }

            BrixNode newNode = (BrixNode) parentNode.addNode(fileName, "nt:file");

            try {
                // copy the upload into a temp file and assign that
                // output stream to the node
                File temp = File.createTempFile(
                        Brix.NS + "-upload-" + UUID.randomUUID().toString(), null);

                Streams.copy(upload.getInputStream(), new FileOutputStream(temp));
                upload.closeStreams();

                String mime = upload.getContentType();

                BrixFileNode file = BrixFileNode.initialize(newNode, mime);
                file.setData(file.getSession().getValueFactory().createBinary(new FileInputStream(temp)));
                file.getParent().save();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }


        SitePlugin.get().selectNode(this, parentNode, true);
    }

    @Override
    protected void onDetach() {
        uploads.clear();
        super.onDetach();
    }
}
