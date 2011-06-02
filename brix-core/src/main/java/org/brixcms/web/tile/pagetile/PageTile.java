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

package org.brixcms.web.tile.pagetile;

import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.model.IModel;
import org.brixcms.Path;
import org.brixcms.jcr.api.JcrNode;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.page.AbstractContainer;
import org.brixcms.plugin.site.page.tile.Tile;
import org.brixcms.plugin.site.page.tile.admin.TileEditorPanel;

import java.util.HashSet;
import java.util.Set;

public class PageTile implements Tile {
    public static String TYPE_NAME = PageTile.class.getName();

    // needed to detect loop during #requiresSSL call
    private static final MetaDataKey<Set<Path>> NODE_SET_KEY = new MetaDataKey<Set<Path>>() {
    };

    public PageTile() {
    }


    public String getDisplayName() {
        return "Page Tile";
    }

    public String getTypeName() {
        return TYPE_NAME;
    }

    public TileEditorPanel newEditor(String id, IModel<BrixNode> containerNode) {
        return new PageTileEditorPanel(id, containerNode);
    }

    public Component newViewer(String id, IModel<BrixNode> tileNode) {
        return new PageTileViewerPanel(id, tileNode);
    }

    public boolean requiresSSL(IModel<BrixNode> tileNode) {
        // get or create set of paths that were already processed
        Set<Path> set = (Set<Path>) RequestCycle.get().getMetaData(NODE_SET_KEY);
        if (set == null) {
            set = new HashSet<Path>();
            RequestCycle.get().setMetaData(NODE_SET_KEY, set);
        }


        Path nodePath = new Path(tileNode.getObject().getParent().getPath());

        if (set.contains(nodePath)) {
            // this means we found a loop. However here we just return false,
            // PageTileViewerPanel is responsible for displaying the error
            return false;
        }
        set.add(nodePath);

        final boolean result;

        if (tileNode.getObject().hasProperty("pageNode")) {
            JcrNode pageNode = tileNode.getObject().getProperty("pageNode").getNode();
            result = ((AbstractContainer) pageNode).requiresSSL();
        } else {
            result = false;
        }


        set.remove(nodePath);
        if (set.isEmpty()) {
            RequestCycle.get().setMetaData(NODE_SET_KEY, null);
        }

        return result;
    }
}
