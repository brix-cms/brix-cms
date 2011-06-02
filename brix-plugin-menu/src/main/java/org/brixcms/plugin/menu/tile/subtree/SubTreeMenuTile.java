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

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

public class SubTreeMenuTile implements Tile {
    public static final String TYPE_NAME = "brix.web.tile.menu.MenuTile";


    public String getDisplayName() {
        return "Menu";
    }

    public String getTypeName() {
        return TYPE_NAME;
    }

    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode) {
        return new MenuTileEditor(id, tileContainerNode);
    }

    public Component newViewer(String id, IModel<BrixNode> tileNode) {
        return new MenuRenderer(id, tileNode);
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode) {
        return false;
    }
}
