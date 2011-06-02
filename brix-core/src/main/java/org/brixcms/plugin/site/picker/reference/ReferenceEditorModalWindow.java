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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.web.generic.IGenericComponent;
import org.brixcms.web.reference.Reference;

public class ReferenceEditorModalWindow extends ModalWindow implements IGenericComponent<Reference> {
    private final ReferenceEditorConfiguration configuration;

    public ReferenceEditorModalWindow(String id, IModel<Reference> model, ReferenceEditorConfiguration configuration) {
        super(id);
        setModel(model);

        this.configuration = configuration;

        setWidthUnit("em");
        setInitialWidth(64);
        setUseInitialHeight(false);
        setResizable(false);
        setCookieName("reference-editor");
        setTitle(new ResourceModel("title"));
    }

    public void setModel(IModel<Reference> model) {
        setDefaultModel(model);
    }


    public Reference getModelObject() {
        return (Reference) getDefaultModelObject();
    }

    public void setModelObject(Reference object) {
        setDefaultModelObject(object);
    }

    @Override
    public void show(AjaxRequestTarget target) {
        if (isShown() == false) {
            initContent();
        }
        super.show(target);
    }

    private void initContent() {
        setContent(new ReferenceEditorWithButtons(getContentId(), getModel(), configuration) {
            @Override
            protected void onCancel(AjaxRequestTarget target) {
                super.onCancel(target);
                ReferenceEditorModalWindow.this.onCancel(target);
            }

            @Override
            protected void onOk(AjaxRequestTarget target) {
                super.onOk(target);
                ReferenceEditorModalWindow.this.onOk(target);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public IModel<Reference> getModel() {
        return (IModel<Reference>) getDefaultModel();
    }

    protected void onCancel(AjaxRequestTarget target) {
        close(target);
    }

    protected void onOk(AjaxRequestTarget target) {
        close(target);
    }
}
