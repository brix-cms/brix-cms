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

package org.brixcms.plugin.site.admin;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.BrixGenericPanel;

public abstract class RenamePanel extends BrixGenericPanel<BrixNode> {
// ------------------------------ FIELDS ------------------------------
    ;
    private String newName;

    public RenamePanel(String id, IModel<BrixNode> model) {
        super(id, model);

        Form<?> form = new Form<Void>("form");

        newName = model.getObject().getName();

        TextField<String> newName = new TextField<String>("newName", new PropertyModel<String>(
                this, "newName"));
        newName.setRequired(true);
        newName.add(new NewNameValidator());
        form.add(newName);

        form.add(new SubmitLink("rename") {
            @Override
            public void onSubmit() {
                JcrNode node = RenamePanel.this.getModelObject();

                if (RenamePanel.this.newName.equals(node.getName()) == false) {
                    node.getSession().move(node.getPath(),
                            node.getParent().getPath() + "/" + RenamePanel.this.newName);
                    node.getSession().save();
                }
                onLeave();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                onLeave();
            }
        });

        form.add(new FeedbackPanel("feedback"));

        add(form);
    }

    protected abstract void onLeave();

    private class NewNameValidator implements IValidator {
        public void validate(IValidatable validatable) {
            String name = (String) validatable.getValue();

            if (getModelObject().getName().equals(name) == false) {
                JcrNode parent = getModelObject().getParent();
                if (parent.hasNode(name)) {
                    validatable.error(new ValidationError().addMessageKey("NewNameValidator")
                            .setVariable("name", name));
                }
            }
        }
    }
}
