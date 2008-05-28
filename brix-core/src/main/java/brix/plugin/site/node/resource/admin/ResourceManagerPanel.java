package brix.plugin.site.node.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.auth.Action;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.node.resource.ResourceManager;
import brix.plugin.site.node.resource.ResourceNodePlugin;

public class ResourceManagerPanel extends NodeManagerPanel
{

    private ResourceManager getManager()
    {

        BrixFileNode node = (BrixFileNode)getModelObject();

        ResourceNodePlugin plugin = (ResourceNodePlugin)SitePlugin.get().getNodePluginForNode(node);

        return plugin.getResourceManagerForMimeType(node.getMimeType());
    }

    public ResourceManagerPanel(String id, final IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        List<ITab> tabs = new ArrayList<ITab>();

        tabs.add(new AbstractTab(new Model("View"))
        {
            @Override
            public Panel getPanel(String panelId)
            {
                return getManager().newViewer(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return getManager() != null && getManager().hasViewer() && hasViewPermission();
            }

        });

        tabs.add(new AbstractTab(new Model("Edit"))
        {
            @Override
            public Panel getPanel(String panelId)
            {
                return getManager().newEditor(panelId, nodeModel);
            }

            @Override
            public boolean isVisible()
            {
                return getManager() != null && getManager().hasEditor() && hasEditPermission();
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
                return hasEditPermission();
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
                return hasViewPermission();
            }
        });

        add(new TabbedPanel("tabbedPanel", tabs));

    }

    private boolean hasViewPermission()
    {
        Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
                SiteNodeAction.Type.NODE_VIEW, getNode());
        return Brix.get().getAuthorizationStrategy().isActionAuthorized(action);
    }

    private boolean hasEditPermission()
    {
        Action action = new SiteNodeAction(Action.Context.ADMINISTRATION,
                SiteNodeAction.Type.NODE_EDIT, getNode());
        return Brix.get().getAuthorizationStrategy().isActionAuthorized(action);
    }

}
