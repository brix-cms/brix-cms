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

package org.brixcms.rmiserver.web.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;

public class GenericFragment<T> extends Fragment {
    public GenericFragment(String id, String markupId, MarkupContainer markupProvider) {
        super(id, markupId, markupProvider);
    }

    public GenericFragment(String id, String markupId, MarkupContainer markupProvider, IModel<T> model) {
        super(id, markupId, markupProvider, model);
    }

    @SuppressWarnings("unchecked")
    public final IModel<T> getModel() {
        return (IModel<T>) getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    public final T getModelObject() {
        return (T) getDefaultModelObject();
    }

    public final void setModel(IModel<T> model) {
        setDefaultModel(model);
    }

    public final void setModelObject(T object) {
        setDefaultModelObject(object);
    }
}
