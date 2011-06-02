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
import org.apache.wicket.model.Model;

/**
 * Default implementation of markup editor factory. Uses a simple textarea.
 *
 * @author igor.vaynberg
 */
public class SimpleMarkupEditorFactory implements MarkupEditorFactory {

    public TextArea<String> newEditor(String id, IModel<String> markup) {
        return new TextArea<String>("content", markup);
    }

    public IModel<String> newLabel() {
        return new Model<String>("Simple Text");
    }
}
