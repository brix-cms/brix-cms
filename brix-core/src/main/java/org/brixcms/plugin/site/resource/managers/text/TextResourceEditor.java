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

package org.brixcms.plugin.site.resource.managers.text;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.jcr.wrapper.BrixNode.Protocol;
import org.brixcms.web.model.ModelBuffer;

import java.util.Arrays;
import java.util.List;

public class TextResourceEditor extends Panel {
    private DropDownChoice<Protocol> requiredProtocol;

    public TextResourceEditor(String id, ModelBuffer model) {
        super(id);

        // protocol field
        List<Protocol> protocols = Arrays.asList(Protocol.values());
        IChoiceRenderer<Protocol> renderer = new ProtocolRenderer();
        IModel<Protocol> protocolModel = model.forProperty("requiredProtocol");
        requiredProtocol = new DropDownChoice<Protocol>("requiredProtocol", protocolModel,
                protocols, renderer);
        add(requiredProtocol.setNullValid(false));

        // mimetype field
        IModel<String> mimeTypeModel = model.forProperty("mimeType");
        add(new TextField<String>("mimeType", mimeTypeModel).add(new TextMimeTypeValidator())
                .setLabel(new ResourceModel("mimeType")));

        // content field
        IModel<String> contentModel = model.forProperty("dataAsString");
        add(new TextArea<String>("content", contentModel));
    }

    @Override
    protected void onBeforeRender() {
        // default require protocol to preserve
        if (requiredProtocol.getModelObject() == null) {
            requiredProtocol.setModelObject(Protocol.PRESERVE_CURRENT);
        }

        super.onBeforeRender();
    }

    private final class ProtocolRenderer implements IChoiceRenderer<Protocol> {
        public Object getDisplayValue(Protocol object) {
            return getString(object.toString());
        }

        public String getIdValue(Protocol object, int index) {
            return object.toString();
        }
    }
}
