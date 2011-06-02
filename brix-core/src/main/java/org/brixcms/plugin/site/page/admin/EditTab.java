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

/**
 *
 */
package org.brixcms.plugin.site.page.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.plugin.site.page.TemplateSiteNodePlugin;
import org.brixcms.plugin.site.picker.node.SiteNodePickerPanel;
import org.brixcms.web.ContainerFeedbackPanel;
import org.brixcms.web.model.ModelBuffer;
import org.brixcms.web.picker.node.NodeTypeFilter;
import org.brixcms.web.tree.NodeFilter;

import java.util.Collection;

abstract class EditTab extends NodeManagerPanel {
    private String currentEditorFactory;
    private final MarkupContainer contentEditorParent;
    private final IModel<String> contentEditorModel;

    public EditTab(String id, final IModel<BrixNode> nodeModel) {
        super(id, nodeModel);

        Brix brix = getModelObject().getBrix();
        Form<Void> form = new Form<Void>("form");
        add(form);

        final ModelBuffer adapter = new ModelBuffer(nodeModel);
        IModel<String> stringModel = adapter.forProperty("title");

        form.add(new TextField<String>("title", stringModel));

        String workspace = nodeModel.getObject().getSession().getWorkspace().getName();
        NodeFilter filter = new NodeTypeFilter(TemplateSiteNodePlugin.TYPE);

        IModel<BrixNode> model = adapter.forNodeProperty("template");

        form.add(new SiteNodePickerPanel("templatePicker", model, workspace, filter));

        IModel<Boolean> booleanModel = adapter.forProperty("requiresSSL");
        form.add(new ProtocolSelector("requiresSSL", booleanModel));

        IModel<String> mimeTypeModel = adapter.forProperty("mimeType");
        form.add(new TextField<String>("mimeType", mimeTypeModel));

        // set up markup editor

        contentEditorModel = adapter.forProperty("dataAsString");
        contentEditorParent = form;

        Collection<MarkupEditorFactory> editorFactories = brix.getConfig().getRegistry()
                .lookupCollection(MarkupEditorFactory.POINT);

        setupEditor(editorFactories.iterator().next().getClass().getName());

        // set up buttons to control editor switching

        RepeatingView editors = new RepeatingView("editors") {
            @Override
            public boolean isVisible() {
                return size() > 1;
            }
        };
        form.add(editors);

        for (MarkupEditorFactory factory : editorFactories) {
            final String cn = factory.getClass().getName();
            editors.add(new Button(editors.newChildId(), factory.newLabel()) {
                public void onSubmit() {
                    setupEditor(cn);
                }

                @Override
                public boolean isEnabled() {
                    return !cn.equals(currentEditorFactory);
                }
            });
        }


        form.add(new ContainerFeedbackPanel("feedback", this));

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                JcrNode node = nodeModel.getObject();
                node.checkout();
                adapter.apply();
                node.save();
                node.checkin();

                getSession().info(getString("status.saved"));
                goBack();
            }
        });

        form.add(new Link<Void>("cancel") {
            @Override
            public void onClick() {
                getSession().info(getString("status.cancelled"));
                goBack();
            }
        });
    }

    private void setupEditor(String cn) {
        final Brix brix = getModelObject().getBrix();

        Collection<MarkupEditorFactory> factories = brix.getConfig().getRegistry()
                .lookupCollection(MarkupEditorFactory.POINT);

        for (MarkupEditorFactory factory : factories) {
            if (factory.getClass().getName().equals(cn)) {
                contentEditorParent.addOrReplace(factory.newEditor("content", contentEditorModel));
                currentEditorFactory = factory.getClass().getName();
                return;
            }
        }

        throw new RuntimeException("Unknown markup editor factory class: " + cn);
    }

    abstract void goBack();

    ;
}