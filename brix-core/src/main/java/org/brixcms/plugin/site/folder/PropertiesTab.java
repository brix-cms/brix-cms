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
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorConfiguration;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorPanel;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.model.ModelBuffer;
import org.brixcms.web.reference.Reference;

public class PropertiesTab extends BrixGenericPanel<BrixNode> {
    public PropertiesTab(String id, final IModel<BrixNode> folderNodeModel) {
        super(id, folderNodeModel);

        final ModelBuffer buffer = new ModelBuffer(folderNodeModel);

        Form<Void> form = new Form<Void>("form");

        form.add(new SubmitLink("submit") {
            @Override
            public void onSubmit() {
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
