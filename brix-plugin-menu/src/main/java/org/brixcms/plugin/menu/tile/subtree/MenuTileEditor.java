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

package org.brixcms.plugin.menu.tile.subtree;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.menu.tile.MenuPicker;
import org.brixcms.plugin.site.page.tile.admin.GenericTileEditorPanel;

class MenuTileEditor extends GenericTileEditorPanel<BrixNode> {
    private MenuContainer currentEntry = new MenuContainer();

    public MenuTileEditor(String id, IModel<BrixNode> containerNode) {
        super(id, containerNode);

        add(new MenuPicker("menuPicker",
                new PropertyModel<BrixNode>(currentEntry, "menuNode"), containerNode));

        Form<MenuContainer> form;
        add(form = new Form<MenuContainer>("form", new CompoundPropertyModel<MenuContainer>(
                new PropertyModel<MenuContainer>(this, "currentEntry"))));

        form.add(new TextField<String>("outerContainerStyleClass"));
        form.add(new TextField<String>("innerContainerStyleClass"));
        form.add(new TextField<String>("selectedItemStyleClass"));
        form.add(new TextField<String>("itemWithSelectedChildStyleClass"));
        form.add(new TextField<Integer>("startAtLevel"));
        form.add(new TextField<Integer>("renderLevels"));
    }

    @Override
    public void load(BrixNode node) {
        currentEntry.load(node);
    }

    @Override
    protected void onDetach() {
        if (currentEntry != null) {
            currentEntry.detach();
        }
        super.onDetach();
    }

    @Override
    public void save(BrixNode node) {
        currentEntry.save(node);
    }
}
