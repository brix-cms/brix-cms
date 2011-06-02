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

package org.brixcms.web.tab;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class CachingAbstractTab extends AbstractBrixTab {
    private Panel panel = null;

    public CachingAbstractTab(IModel<String> title) {
        super(title);
    }

    public CachingAbstractTab(IModel<String> title, int priority) {
        super(title, priority);
    }



    @Override
    public Panel getPanel(String panelId) {
        if (panel == null) {
            panel = newPanel(panelId);
        }
        return panel;
    }

    public boolean isVisible() {
        return true;
    }

    public abstract Panel newPanel(String panelId);
}
