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

package org.brixcms.plugin.site.page.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.auth.Action.Context;
import org.brixcms.jcr.wrapper.BrixNode;
import org.brixcms.plugin.site.ManageNodeTabFactory;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.plugin.site.page.PageSiteNodePlugin;
import org.brixcms.plugin.site.page.TemplateSiteNodePlugin;
import org.brixcms.plugin.site.page.tile.admin.TilesPanel;
import org.brixcms.web.tab.CachingAbstractTab;
import org.brixcms.web.tab.IBrixTab;

import java.util.ArrayList;
import java.util.List;

public class ManageTileNodeTabFactory implements ManageNodeTabFactory
{
    public List<IBrixTab> getManageNodeTabs(IModel<BrixNode> nodeModel)
    {
        String type = nodeModel.getObject().getNodeType();
        if (PageSiteNodePlugin.TYPE.equals(type) || TemplateSiteNodePlugin.TYPE.equals(type))
        {
            return getTabs(nodeModel);
        }
        else
        {
            return null;
        }
    }

    private static List<IBrixTab> getTabs(final IModel<BrixNode> nodeModel)
    {
        List<IBrixTab> tabs = new ArrayList<IBrixTab>();
        
        tabs.add(new CachingAbstractTab(new ResourceModel("view", "View"))
        {

            @Override
            public Panel newPanel(String panelId)
            {
                return new ViewTab(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return hasViewPermission(nodeModel);
            }

        });

        tabs.add(new CachingAbstractTab(new ResourceModel("tiles", "Tiles"))
        {

            @Override
            public Panel newPanel(String panelId)
            {
                return new TilesPanel(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission(nodeModel);
            }

        });
        
        tabs.add(new CachingAbstractTab(new ResourceModel("variables", "Variables"))
        {

            @Override
            public Panel newPanel(String panelId)
            {
                return new VariablesPanel(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission(nodeModel);
            }

        });

        return tabs;
    }

    private static boolean hasViewPermission(IModel<BrixNode> nodeModel)
    {
    	return SitePlugin.get().canViewNode(nodeModel.getObject(), Context.ADMINISTRATION);        
    }

    private static boolean hasEditPermission(IModel<BrixNode> nodeModel)
    {
    	return SitePlugin.get().canEditNode(nodeModel.getObject(), Context.ADMINISTRATION);
    }

}
