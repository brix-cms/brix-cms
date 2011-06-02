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

package org.brixcms.plugin.menu.editor.cell;

import com.inmethod.grid.column.AbstractColumn;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.plugin.menu.Menu;
import org.brixcms.plugin.site.picker.reference.ReferenceEditorConfiguration;
import org.brixcms.web.reference.Reference;

/**
 * Created by IntelliJ IDEA. User: korbinianbachl Date: 08.09.2010 Time: 21:11:23
 */
public class SwitcherColumn extends AbstractColumn {
    ReferenceEditorConfiguration conf;

    public SwitcherColumn(String id, IModel<String> displayModel, ReferenceEditorConfiguration conf) {
        super(id, displayModel);
        this.conf = conf;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Component newCell(WebMarkupContainer parent, String componentId, final IModel rowModel) {
        IModel<Menu.ChildEntry.MenuType> typeModel = new PropertyModel<Menu.ChildEntry.MenuType>(rowModel, "entry.menuType");
        IModel<Reference> referenceModel = new PropertyModel<Reference>(rowModel, "entry.reference");
        IModel<String> labelOrCodeModel = new PropertyModel<String>(rowModel, "entry.labelOrCode");

        return new SwitcherCellPanel(componentId, typeModel, referenceModel, labelOrCodeModel, conf) {
            @Override
            boolean isEditing() {
                return getGrid().isItemEdited(rowModel);
            }
        };
    }
}
