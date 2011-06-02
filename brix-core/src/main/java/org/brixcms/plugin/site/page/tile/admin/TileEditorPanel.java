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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.brixcms.jcr.wrapper.BrixNode;

public abstract class TileEditorPanel extends Panel {
    public TileEditorPanel(String id) {
        super(id);
    }

    public TileEditorPanel(String id, IModel<?> model) {
        super(id, model);
    }

    abstract public void load(BrixNode node);

    abstract public void save(BrixNode node);
}
