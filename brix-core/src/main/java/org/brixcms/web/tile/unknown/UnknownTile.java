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

package org.brixcms.web.tile.unknown;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

public class UnknownTile implements Tile {
    public static final UnknownTile INSTANCE = new UnknownTile();


    public String getDisplayName() {
        return "Unknown";
    }

    public String getTypeName() {
        return UnknownTile.class.getName();
    }

    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode) {
        return new Editor(id);
    }

    public Component newViewer(String id, IModel<BrixNode> tileNode) {
        return new Label(id, "Unknown Tile");
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode) {
        return false;
    }

    private static class Editor extends TileEditorPanel {
        public Editor(String id) {
            super(id);
        }

        @Override
        public void load(BrixNode node) {

        }

        @Override
        public void save(BrixNode node) {

        }
    }
}
