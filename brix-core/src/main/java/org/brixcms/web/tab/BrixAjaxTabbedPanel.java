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

import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;

import java.util.List;

public class BrixAjaxTabbedPanel extends AjaxTabbedPanel {
    public BrixAjaxTabbedPanel(String id, List<IBrixTab> tabs) {
        super(id, BrixTabbedPanel.sort(tabs));
    }

    @Override
    protected String getTabContainerCssClass() {
        return "brix-tab-row";
    }
}
