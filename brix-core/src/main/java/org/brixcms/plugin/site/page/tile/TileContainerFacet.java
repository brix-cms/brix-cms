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

package org.brixcms.plugin.site.page.tile;

import org.apache.wicket.model.IModel;
import org.brixcms.Brix;
import org.brixcms.BrixNodeModel;
import org.brixcms.jcr.api.JcrNodeIterator;
import org.brixcms.jcr.wrapper.BrixNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for managing node's tile collection
 *
 * @author ivaynberg
 */
public class TileContainerFacet {
    /**
     * Name of the Tile nodes(s)
     */
    public static final String TILE_NODE_NAME = Brix.NS_PREFIX + "tile";

    /**
     * JCR type of Tile nodes
     */
    public static final String JCR_TYPE_BRIX_TILE = Brix.NS_PREFIX + "tile";


    private final BrixNode container;

    public TileContainerFacet(BrixNode container) {
        this.container = container;
    }

    public boolean anyTileRequiresSSL() {
        List<BrixNode> tiles = getTileNodes();
        for (BrixNode tileNode : tiles) {
            String className = TileContainerFacet.getTileClassName(tileNode);
            Tile tile = Tile.Helper.getTileOfType(className, container.getBrix());
            IModel<BrixNode> tileNodeModel = new BrixNodeModel(tileNode);
            if (tile.requiresSSL(tileNodeModel)) {
                return true;
            }
        }
        return false;
    }

    public List<BrixNode> getTileNodes() {
        List<BrixNode> result = new ArrayList<BrixNode>();
        JcrNodeIterator iterator = container.getNodes(TILE_NODE_NAME);
        while (iterator.hasNext()) {
            BrixNode node = (BrixNode) iterator.nextNode();
            if (node.isNodeType(JCR_TYPE_BRIX_TILE)) {
                result.add(node);
            }
        }
        return result;
    }

    public static String getTileClassName(BrixNode tile) {
        if (tile.hasProperty(Properties.TILE_CLASS)) {
            return tile.getProperty(Properties.TILE_CLASS).getString();
        } else {
            return null;
        }
    }

    public BrixNode createTile(String tileId, String typeName) {
        if (tileId == null) {
            throw new IllegalArgumentException("Argument 'tileId' may not be null.");
        }
        if (typeName == null) {
            throw new IllegalArgumentException("Argument 'typeName' may not be null.");
        }

        // TODO this check needs to be fixed?
        // if (isValidNodeName(tileId) == false)
        // {
        // throw new IllegalArgumentException("Argument 'tileId' is not a valid node name.");
        // }
        // if (hasNode(tileId))
        // {
        // throw new BrixException("Tile with id '" + tileId + "' already exists.");
        // }

        BrixNode tile = (BrixNode) container.addNode(TILE_NODE_NAME, JCR_TYPE_BRIX_TILE);

        tile.setProperty(Properties.TILE_ID, tileId);
        tile.setProperty(Properties.TILE_CLASS, typeName);

        return tile;
    }

    public String getTileClassName(String tileId) {
        BrixNode tile = getTile(tileId);
        if (tile != null) {
            return getTileClassName(tile);
        } else {
            return null;
        }
    }

    public BrixNode getTile(String id) {
        if (id == null) {
            throw new IllegalArgumentException("tile id cannot be null");
        }
        JcrNodeIterator iterator = container.getNodes(TILE_NODE_NAME);
        while (iterator.hasNext()) {
            BrixNode node = (BrixNode) iterator.nextNode();
            if (node.isNodeType(JCR_TYPE_BRIX_TILE) && id.equals(getTileId(node))) {
                return node;
            }
        }
        return null;
    }

    public static String getTileId(BrixNode tile) {
        if (tile.hasProperty(Properties.TILE_ID)) {
            return tile.getProperty(Properties.TILE_ID).getString();
        } else {
            return null;
        }
    }

    private static class Properties {
        public static final String TILE_ID = Brix.NS_PREFIX + "tileId";
        public static final String TILE_CLASS = Brix.NS_PREFIX + "tileClass";
    }

    ;
}
