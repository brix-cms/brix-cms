package brix.plugin.site.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.auth.Action;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.ManageNodeTabFactory;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.resource.ResourceManager;
import brix.plugin.site.resource.ResourceNodePlugin;

public class ManageResourceNodeTabFactory implements ManageNodeTabFactory
{

    private static ResourceManager getManager(IModel<BrixNode> nodeModel)
    {
        BrixFileNode node = (BrixFileNode)nodeModel.getObject();

        ResourceNodePlugin plugin = (ResourceNodePlugin)SitePlugin.get().getNodePluginForNode(node);

        return plugin.getResourceManagerForMimeType(node.getMimeType());
    }

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

        tabs.add(new AbstractTab(new Model("View"))
        {
            @Override
            public Panel getPanel(String panelId)
            {
                return getManager(nodeModel).newViewer(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                ResourceManager manager = getManager(nodeModel);
                return manager != null && manager.hasViewer() && hasViewPermission(nodeModel);
            }

        });

        tabs.add(new AbstractTab(new Model("Edit"))
        {
            @Override
            public Panel getPanel(String panelId)
            {
                return getManager(nodeModel).newEditor(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                ResourceManager manager = getManager(nodeModel);
                return manager != null && manager.hasEditor() && hasEditPermission(nodeModel);
            }
        });

        tabs.add(new AbstractTab(new Model("Properties"))
        {
            @Override
            public Panel getPanel(String panelId)
            {
                return new PropertiesTab(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission(nodeModel);
            }
        });

        tabs.add(new AbstractTab(new Model("Download"))
        {
            @Override
            public Panel getPanel(String panelId)
            {
                return new DownloadTab(panelId, nodeModel);
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
        Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
            SiteNodeAction.Type.NODE_VIEW, model.getObject());
        return model.getObject().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
    }

    private static boolean hasEditPermission(IModel<BrixNode> model)
    {
        Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
            SiteNodeAction.Type.NODE_EDIT, model.getObject());
        return model.getObject().getBrix().getAuthorizationStrategy().isActionAuthorized(action);
    }

}
