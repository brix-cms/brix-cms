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

package org.brixcms.plugin.site.folder;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

import java.util.ArrayList;
import java.util.List;

public class ManageFolderNodeTabFactory implements ManageNodeTabFactory {
    public static List<IBrixTab> getTabs(final IModel<BrixNode> folderModel) {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>(2);
        tabs.add(new CachingAbstractTab(new ResourceModel("listing", "Listing"), 100) {
            @Override
            public Panel newPanel(String panelId) {
                return new ListFolderNodesTab(panelId, folderModel);
            }

            @Override
            public boolean isVisible() {
                return SitePlugin.get().canViewNodeChildren(folderModel.getObject(), Context.ADMINISTRATION);
            }
        });
        tabs.add(new CachingAbstractTab(new ResourceModel("properties", "Properties")) {
            @Override
            public Panel newPanel(String panelId) {
                return new PropertiesTab(panelId, folderModel);
            }

            @Override
            public boolean isVisible() {
                return SitePlugin.get().canEditNode(folderModel.getObject(), Context.ADMINISTRATION);
            }
        });
        return tabs;
    }


    public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
        if (nodeModel.getObject().isFolder()) {
            return getTabs(nodeModel);
        } else {
            return null;
        }
    }
}
