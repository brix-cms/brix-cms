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

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.jcr.wrapper.BrixNode.Protocol;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.model.ModelBuffer;

import java.util.Arrays;
import java.util.List;

public abstract class EditPropertiesPanel extends BrixGenericPanel<BrixNode> {
    public EditPropertiesPanel(String id, final IModel<BrixNode> nodeModel) {
        super(id, nodeModel);

        List<Protocol> protocols = Arrays.asList(Protocol.values());

        final ModelBuffer model = new ModelBuffer(nodeModel);
        Form<?> form = new Form<Void>("form");

        IChoiceRenderer<Protocol> renderer = new IChoiceRenderer<Protocol>() {
            public Object getDisplayValue(Protocol object) {
                return getString(object.toString());
            }

            public String getIdValue(Protocol object, int index) {
                return object.toString();
            }
        };
        IModel<Protocol> protocolModel = model.forProperty("requiredProtocol");
        form.add(new DropDownChoice<Protocol>("requiredProtocol", protocolModel, protocols,
                renderer).setNullValid(false));

        IModel<String> mimeTypeModel = model.forProperty("mimeType");
        form.add(new TextField<String>("mimeType", mimeTypeModel));

        form.add(new SubmitLink("save") {
            @Override
            public void onSubmit() {
                BrixNode node = nodeModel.getObject();
                model.apply();
                node.save();
                getSession().info(getString("propertiesSaved"));
                goBack();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                getSession().info(getString("editingCanceled"));
                goBack();
            }
        });

        add(form);
    }

    abstract void goBack();
}
