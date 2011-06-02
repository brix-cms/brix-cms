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

package org.brixcms.plugin.site.folder;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.Path;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.util.validators.NodeNameValidator;

import java.io.Serializable;

public class CreateFolderPanel extends BrixGenericPanel<BrixNode> {
    private String name;

    public CreateFolderPanel(String id, IModel<BrixNode> model, final SimpleCallback goBack) {
        super(id, model);

        Form<?> form = new Form<CreateFolderPanel>("form", new CompoundPropertyModel<CreateFolderPanel>(this));
        add(form);

        form.add(new ContainerFeedbackPanel("feedback", this));

        form.add(new SubmitLink("create") {
            @Override
            public void onSubmit() {
                createFolder();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                goBack.execute();
            }
        });

        final TextField<String> tf;
        form.add(tf = new TextField<String>("name"));
        tf.setRequired(true);
        tf.add(NodeNameValidator.getInstance());
    }

    private void createFolder() {
        final JcrNode parent = (JcrNode) getModelObject();

        final Path path = new Path(parent.getPath());
        final Path newPath = path.append(new Path(name));

        final JcrSession session = parent.getSession();

        if (session.itemExists(newPath.toString())) {
            class ModelObject implements Serializable {
                @SuppressWarnings("unused")
                public String path = SitePlugin.get().fromRealWebNodePath(newPath.toString());
            }

            ;
            String error = getString("resourceExists", new Model<ModelObject>(new ModelObject()));
            error(error);
        } else {
            FolderNode node = (FolderNode) parent.addNode(name, "nt:folder");
            parent.save();

            SitePlugin.get().selectNode(this, node, true);
        }
    }
}
