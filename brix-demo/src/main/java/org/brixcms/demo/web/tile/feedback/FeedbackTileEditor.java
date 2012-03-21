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

package org.brixcms.demo.web.tile.feedback;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

public class FeedbackTileEditor extends TileEditorPanel {

    private FeedbackTileConfiguration configuration = new FeedbackTileConfiguration();

    public FeedbackTileEditor(String id, IModel<BrixNode> tileContainerNode) {
        super(id);
        add(new TextField<String>("email", new PropertyModel<String>(configuration, "email")).setRequired(true)
                .add(EmailAddressValidator.getInstance()).setLabel(new ResourceModel("email")));
        add(new TextField<String>("feedbackEmailLabel", new PropertyModel<String>(configuration, "feedbackEmailLabel"))
                .setLabel(new ResourceModel("feedbackEmailLabel")));
        add(new TextField<String>("feedbackMessageLabel", new PropertyModel<String>(configuration, "feedbackMessageLabel"))
                .setLabel(new ResourceModel("feedbackMessageLabel")));
        add(new TextField<String>("feedbackSubmittedMessage", new PropertyModel<String>(configuration, "feedbackSubmittedMessage"))
                .setLabel(new ResourceModel("feedbackSubmittedMessage")).setRequired(true));
    }

    @Override
    public void load(BrixNode node) {
        configuration.load(node);
    }

    @Override
    public void save(BrixNode node) {
        configuration.save(node);
    }
}
