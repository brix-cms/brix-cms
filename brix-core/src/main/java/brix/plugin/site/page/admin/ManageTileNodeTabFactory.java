package brix.plugin.site.page.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.page.PageSiteNodePlugin;
import brix.plugin.site.page.TemplateSiteNodePlugin;
import brix.plugin.site.page.tile.admin.TilesPanel;
import brix.web.tab.CachingAbstractTab;
import brix.web.tab.IBrixTab;

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
        
        // TODO: Externalize strings
        
        tabs.add(new CachingAbstractTab(new Model<String>("View"))
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

        tabs.add(new CachingAbstractTab(new Model<String>("Tiles"))
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
        
        tabs.add(new CachingAbstractTab(new Model<String>("Variables"))
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
