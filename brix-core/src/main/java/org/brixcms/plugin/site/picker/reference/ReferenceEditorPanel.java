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

package org.brixcms.plugin.site.picker.reference;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.web.reference.Reference;

public class ReferenceEditorPanel extends FormComponentPanel<Reference> {
    protected static final String MODAL_WINDOW_ID = "modalWindow";

    private ReferenceEditorConfiguration configuration;

    public ReferenceEditorPanel(String id) {
        super(id);
    }

    public ReferenceEditorPanel(String id, IModel<Reference> model) {
        super(id, model);
    }

    public ReferenceEditorConfiguration getConfiguration() {
        return configuration;
    }


    @Override
    public void updateModel() {
        // don't you dare!
    }

    @Override
    public boolean checkRequired() {
        if (isRequired()) {
            Reference ref = (Reference) getModelObject();
            if (ref == null || ref.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    protected ReferenceEditorModalWindow getModalWindow() {
        return (ReferenceEditorModalWindow) get(MODAL_WINDOW_ID);
    }

    @Override
    public boolean isInputNullable() {
        return false;
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (!hasBeenRendered()) {
            init();
        }
    }

    private void init() {
        add(newModalWindow(MODAL_WINDOW_ID));
        final Label label = new Label("label", newLabelModel()) {
            @Override
            public boolean isVisible() {
                Reference ref = ReferenceEditorPanel.this.getModelObject();
                return ref != null && !ref.isEmpty();
            }
        };
        setOutputMarkupId(true);
        add(label);

        add(new AjaxLink<Void>("edit") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getModalWindow().setModel(ReferenceEditorPanel.this.getModel());
                getModalWindow().setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
                    public void onClose(AjaxRequestTarget target) {
                        target.addComponent(ReferenceEditorPanel.this);
                        ReferenceEditorPanel.this.onUpdate(target);
                    }
                });
                getModalWindow().show(target);
            }
        });

        add(new AjaxLink<Void>("clear") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Reference ref = ReferenceEditorPanel.this.getModelObject();
                ref.makeEmpty();
                // indicate that reference was changed (might be needed if the
                // model is buffered)
                ReferenceEditorPanel.this.setModelObject(ref);
                target.addComponent(ReferenceEditorPanel.this);
                ReferenceEditorPanel.this.onUpdate(target);
            }

            @Override
            public boolean isEnabled() {
                Reference ref = ReferenceEditorPanel.this.getModelObject();
                return ref != null && !ref.isEmpty();
            }
        });
    }

    protected Component newModalWindow(String id) {
        ReferenceEditorConfiguration conf = getConfiguration();
        if (conf == null) {
            throw new IllegalStateException("ReferenceEditorPanel must have configuration.");
        }
        return new ReferenceEditorModalWindow(id, getModel(), getConfiguration());
    }

    protected IModel<String> newLabelModel() {
        return new PropertyModel<String>(getModel(), "generateUrl");
    }

    protected void onUpdate(AjaxRequestTarget target) {

    }

    public ReferenceEditorPanel setConfiguration(ReferenceEditorConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }
}
