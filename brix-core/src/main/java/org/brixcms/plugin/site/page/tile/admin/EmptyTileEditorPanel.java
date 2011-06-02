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

package org.brixcms.plugin.site.page.tile.admin;

import org.brixcms.jcr.wrapper.BrixNode;

/**
 * Provides a default empty editor panel for tiles that have no configuration options
 *
 * @author igor.vaynberg
 */
public class EmptyTileEditorPanel extends TileEditorPanel {
    /**
     * Constructor
     *
     * @param id
     */
    public EmptyTileEditorPanel(String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(BrixNode node) {
        // noop
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(BrixNode node) {// noop
    }
}
