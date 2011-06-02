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

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.model.ModelBuffer;

public abstract class EditTextResourcePanel extends BrixGenericPanel<BrixNode> {
    public EditTextResourcePanel(String id, IModel<BrixNode> node) {
        super(id, node);

        add(new FeedbackPanel("feedback"));

        Form<?> form = new Form<Void>("form");
        add(form);

        final ModelBuffer model = new ModelBuffer(node);

        form.add(new TextResourceEditor("editor", model));

        form.add(new SubmitLink("save") {
            @Override
            public void onSubmit() {
                model.apply();
                getNode().save();
                // done
                getSession().info(getString("saved"));
                done();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                getSession().info(getString("cancelled"));
                done();
            }
        });
    }

    protected abstract void done();

    protected BrixNode getNode() {
        return getModelObject();
    }
}
