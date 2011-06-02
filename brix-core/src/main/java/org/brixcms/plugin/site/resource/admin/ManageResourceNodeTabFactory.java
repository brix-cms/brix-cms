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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.resource.ResourceNodePlugin;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

import java.util.ArrayList;
import java.util.List;

public class ManageResourceNodeTabFactory implements ManageNodeTabFactory {
    private static List<IBrixTab> getTabs(final IModel<BrixNode> nodeModel) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>();

        tabs.add(new CachingAbstractTab(new ResourceModel("properties", "Properties")) {
            @Override
            public Panel newPanel(String panelId) {
                return new ViewPropertiesTab(panelId, nodeModel);
            }

            @Override
            public boolean isVisible() {
                return hasViewPermission(nodeModel);
            }
        });

        return tabs;
    }

    private static boolean hasViewPermission(IModel<BrixNode> model) {
        return SitePlugin.get().canViewNode(model.getObject(), Context.ADMINISTRATION);
    }


    public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
        if (ResourceNodePlugin.TYPE.equals(nodeModel.getObject().getNodeType())) {
            return getTabs(nodeModel);
        } else {
            return null;
        }
    }
}
