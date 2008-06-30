package brix.plugin.site.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.resource.ResourceNodePlugin;

public class ManageResourceNodeTabFactory implements ManageNodeTabFactory
{

    public List<ITab> getManageNodeTabs(IModel<BrixNode> nodeModel)
    {
        if (ResourceNodePlugin.TYPE.equals(nodeModel.getObject().getNodeType()))
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

        tabs.add(new AbstractTab(new Model<String>("Properties"))
        {
            @Override
            public Panel getPanel(String panelId)
            {
                return new ViewPropertiesTab(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return hasViewPermission(nodeModel);
            }
        });

        return tabs;
    }

    private static boolean hasViewPermission(IModel<BrixNode> model)
    {
    	return SitePlugin.get().canViewNode(model.getObject(), Context.ADMINISTRATION);
    }

}
