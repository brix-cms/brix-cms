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

package brix.demo.web.tile.stockquote.stateful;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import brix.demo.web.tile.stockquote.stateless.StatelessStockQuoteTile;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.Tile;
import brix.plugin.site.page.tile.admin.EmptyTileEditorPanel;
import brix.plugin.site.page.tile.admin.TileEditorPanel;

/**
 * Stateful stock quote tile definition.
 * 
 * This tile is stateful because its {@link StatefulStockQuotePanel} keeps state like a regular
 * Wicket component and thus gains all the advantages of Wicket's automatic state management.
 * 
 * Brix supports both stateful and stateless tiles, for a stateless variant of this tile see
 * {@link StatelessStockQuoteTile}
 * 
 * @author igor.vaynberg
 */
public class StatefulStockQuoteTile implements Tile
{

    /** {@inheritDoc} */
    public String getDisplayName()
    {
        return "Stateful Quote";
    }

    /** {@inheritDoc} */
    public String getTypeName()
    {
        return "brix.demo.StatefulStockQuoteTile";
    }

    /** {@inheritDoc} */
    public TileEditorPanel newEditor(String id, IModel<BrixNode> tileContainerNode)
    {
        // this tile has no config options
        return new EmptyTileEditorPanel(id);
    }

    /** {@inheritDoc} */
    public Component newViewer(String id, IModel<BrixNode> tileNode)
    {
        // create and return a panel that will render the tile
        return new StatefulStockQuotePanel(id);
    }

    /** {@inheritDoc} */
    public boolean requiresSSL(IModel<BrixNode> tileNode)
    {
        return false;
    }

}
