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

package org.brixcms.plugin.site.resource.admin;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.SimpleCallback;
import org.brixcms.plugin.site.admin.NodeManagerPanel;
import org.brixcms.plugin.site.resource.managers.text.CreateTextResourcePanel;
import org.brixcms.web.tab.AbstractBrixTab;

import java.util.ArrayList;

public class CreateResourcePanel extends NodeManagerPanel {
    public CreateResourcePanel(String id, final IModel<BrixNode> container,
                               final SimpleCallback back) {
        super(id, container);

        ArrayList<ITab> tabs = new ArrayList<ITab>();

        tabs.add(new AbstractBrixTab(new ResourceModel("upload")) {
            @Override
            public Panel getPanel(String panelId) {
                return new UploadResourcesPanel(panelId, container, back);
            }
        });

        tabs.add(new AbstractBrixTab(new ResourceModel("createText")) {
            @Override
            public Panel getPanel(String panelId) {
                return new CreateTextResourcePanel(panelId, container, back);
            }
        });

        tabs.trimToSize();
        add(new TabbedPanel("tabs", tabs) {
            @Override
            protected String getTabContainerCssClass() {
                return "brix-site-manager-tab-row";
            }
        });
    }
}
