package brix.plugin.site.page.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.page.PageSiteNodePlugin;
import brix.plugin.site.page.TemplateSiteNodePlugin;
import brix.plugin.site.page.tile.admin.TilesPanel;

public class ManageTileNodeTabFactory implements ManageNodeTabFactory
{
    public List<ITab> getManageNodeTabs(IModel<BrixNode> nodeModel)
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

    public int getPriority()
    {
        return 0;
    }

    private static List<ITab> getTabs(final IModel<BrixNode> nodeModel)
    {
        List<ITab> tabs = new ArrayList<ITab>();
        
        // TODO: Externalize strings
        
        tabs.add(new AbstractTab(new Model<String>("View"))
        {

            @Override
            public Panel<?> getPanel(String panelId)
            {
                return new ViewTab(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return hasViewPermission(nodeModel);
            }

        });

        tabs.add(new AbstractTab(new Model<String>("Tiles"))
        {

            @Override
            public Panel<?> getPanel(String panelId)
            {
                return new TilesPanel(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission(nodeModel);
            }

        });
        
        tabs.add(new AbstractTab(new Model<String>("Variables"))
        {

            @Override
            public Panel<?> getPanel(String panelId)
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
        Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
            SiteNodeAction.Type.NODE_VIEW, nodeModel.getObject());
        return nodeModel.getObject().getBrix().getAuthorizationStrategy()
            .isActionAuthorized(action);
    }

    private static boolean hasEditPermission(IModel<BrixNode> nodeModel)
    {
        Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
            SiteNodeAction.Type.NODE_EDIT, nodeModel.getObject());
        return nodeModel.getObject().getBrix().getAuthorizationStrategy()
            .isActionAuthorized(action);
    }

}
