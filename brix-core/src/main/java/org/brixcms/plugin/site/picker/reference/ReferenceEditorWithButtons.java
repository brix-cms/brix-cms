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
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.web.reference.Reference;

public class ReferenceEditorWithButtons extends BrixGenericPanel<Reference> {
    private Reference reference;

    public ReferenceEditorWithButtons(String id, ReferenceEditorConfiguration configuration) {
        super(id);
        init(configuration);
    }

    private void init(ReferenceEditorConfiguration configuration) {
        Reference old = getModelObject();

        reference = old != null ? new Reference(old) : new Reference();

        add(new ReferenceEditor("editor", new PropertyModel<Reference>(this, "reference"), configuration));

        add(new AjaxLink<Void>("ok") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onOk(target);
            }
        });

        add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onCancel(target);
            }
        });
    }

    protected void onOk(AjaxRequestTarget target) {
        setModelObject(getEditedReference());
    }

    protected Reference getEditedReference() {
        return reference;
    }

    protected void onCancel(AjaxRequestTarget target) {

    }

    public ReferenceEditorWithButtons(String id, IModel<Reference> model,
                                      ReferenceEditorConfiguration configuration) {
        super(id, model);
        init(configuration);
    }
}
