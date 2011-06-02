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

package org.brixcms.plugin.site.webdav;

import com.inmethod.grid.column.AbstractColumn;
import com.inmethod.grid.column.editable.EditableCellPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.brixcms.plugin.site.webdav.Rule.Type;

import java.util.Arrays;
import java.util.List;

public class TypePanel extends EditableCellPanel {
    private IChoiceRenderer<Type> typeRenderer = new IChoiceRenderer<Type>() {
        public Object getDisplayValue(Type object) {
            return getString(object.toString());
        }

        public String getIdValue(Type object, int index) {
            return "" + index;
        }
    };

    public TypePanel(String id, AbstractColumn column, IModel rowModel, IModel itemModel) {
        super(id, column, rowModel);

        List<Type> types = Arrays.asList(Type.values());

        DropDownChoice<Type> choice;
        add(choice = new DropDownChoice<Type>("dropDown", itemModel, types, typeRenderer));
        choice.setNullValid(false);
        choice.setRequired(true);
    }

    @Override
    protected FormComponent getEditComponent() {
        return (FormComponent) get("dropDown");
    }
}
