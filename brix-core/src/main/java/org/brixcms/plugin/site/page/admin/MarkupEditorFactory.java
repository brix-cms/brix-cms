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

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.brixcms.registry.ExtensionPoint;

/**
 * A factory that can create an editor to edit markup. The user can then chose from the list of available editors when
 * editing markup throught he web interface.
 *
 * @author igor.vaynberg
 */
public interface MarkupEditorFactory {
    /**
     * Extension point used to register repository initializers
     */
    public static final ExtensionPoint<MarkupEditorFactory> POINT = new ExtensionPoint<MarkupEditorFactory>() {
        public Multiplicity getMultiplicity() {
            return Multiplicity.COLLECTION;
        }

        public String getUuid() {
            return MarkupEditorFactory.class.getName();
        }
    };

    /**
     * Create the textarea component that will represent the editor
     *
     * @param id     component id
     * @param markup markup model
     * @return editor component
     */
    TextArea<String> newEditor(String id, IModel<String> markup);

    /**
     * Create a model that will display the editor name in the menu
     *
     * @return
     */
    IModel<String> newLabel();
}
