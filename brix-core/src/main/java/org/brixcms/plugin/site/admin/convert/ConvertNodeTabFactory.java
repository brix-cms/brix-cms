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

package org.brixcms.plugin.site.admin.convert;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.SiteNodePlugin;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConvertNodeTabFactory implements ManageNodeTabFactory {
    private static IBrixTab newTab(final IModel<BrixNode> nodeModel) {
        return new CachingAbstractTab(new ResourceModel("convert", "Convert"), -1) {
            @Override
            public Panel newPanel(String panelId) {
                return new ConvertTab(panelId, nodeModel);
            }
        };
    }

    private static boolean hasConverterForNode(IModel<BrixNode> nodeModel) {
        Collection<SiteNodePlugin> plugins = SitePlugin.get().getNodePlugins();
        BrixNode node = nodeModel.getObject();
        for (SiteNodePlugin plugin : plugins) {
            if (plugin.getConverterForNode(node) != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasEditPermission(IModel<BrixNode> nodeModel) {
        return SitePlugin.get().canEditNode(nodeModel.getObject(), Context.ADMINISTRATION);
    }


    public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel) {
        List<IBrixTab> result = new ArrayList<IBrixTab>();

        if (hasEditPermission(nodeModel) && hasConverterForNode(nodeModel)) {
            result.add(newTab(nodeModel));
        }

        return result;
    }
}
