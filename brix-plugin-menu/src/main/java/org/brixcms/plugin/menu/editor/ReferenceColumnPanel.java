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

package org.brixcms.plugin.menu.editor;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorConfiguration;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorPanel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.reference.Reference;
import org.brixcms.web.util.AbstractModel;

public abstract class ReferenceColumnPanel extends BrixGenericPanel<Reference> {
    public ReferenceColumnPanel(String id, IModel<Reference> model) {
        super(id, model);

        IModel<String> labelModel = new AbstractModel<String>() {
            @Override
            public String getObject() {
                Reference reference = ReferenceColumnPanel.this.getModelObject();
                if (reference != null && !reference.isEmpty()) {
                    return reference.generateUrl();
                } else {
                    return "";
                }
            }
        };
        final Label label = new Label("label", labelModel);
        add(label);
        label.setOutputMarkupId(true);

        ReferenceEditorPanel editor = new ReferenceEditorPanel("editor", model) {
            @Override
            public ReferenceEditorConfiguration getConfiguration() {
                return ReferenceColumnPanel.this.getConfiguration();
            }

            @Override
            public boolean isVisible() {
                return isEditing();
            }

            @Override
            protected IModel<String> newLabelModel() {
                return new AbstractModel<String>() {
                    @Override
                    public String getObject() {
                        return "";
                    }
                };
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                super.onUpdate(target);
                target.addComponent(label);
            }
        };

        editor.setOutputMarkupPlaceholderTag(true);
        add(editor);
    }

    protected abstract ReferenceEditorConfiguration getConfiguration();

    protected abstract boolean isEditing();
}
