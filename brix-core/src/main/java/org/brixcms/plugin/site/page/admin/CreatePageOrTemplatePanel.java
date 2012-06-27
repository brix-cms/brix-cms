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

package org.brixcms.plugin.site.page.admin;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.plugin.site.page.AbstractContainer;
import org.brixcms.plugin.site.page.PageNode;
import org.brixcms.plugin.site.page.PageSiteNodePlugin;
import org.brixcms.plugin.site.page.TemplateNode;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.util.validators.NodeNameValidator;

public class CreatePageOrTemplatePanel extends NodeManagerPanel {
    private String name;

    public CreatePageOrTemplatePanel(String id, IModel<BrixNode> containerNodeModel, final String type,
                                     final SimpleCallback goBack) {
        super(id, containerNodeModel);

        String typeName = SitePlugin.get().getNodePluginForType(type).getName();
        add(new Label("typeName", typeName));

        Form<?> form = new Form<CreatePageOrTemplatePanel>("form",
                new CompoundPropertyModel<CreatePageOrTemplatePanel>(this));
        add(form);

        form.add(new ContainerFeedbackPanel("feedback", this));

        form.add(new SubmitLink("create") {
            @Override
            public void onSubmit() {
                createPage(type);
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

    private void createPage(String type) {
        final JcrNode parent = getModelObject();

        if (parent.hasNode(name)) {
            String error = getString("resourceExists", new Model<CreatePageOrTemplatePanel>(
                    CreatePageOrTemplatePanel.this));
            error(error);
        } else {
            JcrNode page = parent.addNode(name, "nt:file");

            AbstractContainer node;

            node = initializePage(type, page);

            node.setTitle(name);

            node.setData("");
            name = null;

            parent.save();

            SitePlugin.get().selectNode(this, node, true);
        }
    }

    protected AbstractContainer initializePage(String type, JcrNode page) {
        AbstractContainer node;
        if (type.equals(PageSiteNodePlugin.TYPE)) {
            node = PageNode.initialize(page);
        } else {
            node = TemplateNode.initialize(page);
        }
        return node;
    }
}
