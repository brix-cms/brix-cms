package brix.plugin.site.node.resource.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.auth.NodeAction;
import brix.auth.impl.NodeActionImpl;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixFileNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.node.resource.ResourceManager;
import brix.plugin.site.node.resource.ResourceNodePlugin;
import brix.web.tab.AbstractBrixTab;
import brix.web.tab.BrixTab;
import brix.web.tab.BrixTabbedPanel;

public class ResourceManagerPanel extends NodeManagerPanel
{

    private ResourceManager getManager()
    {

        BrixFileNode node = (BrixFileNode)getModelObject();

        ResourceNodePlugin plugin = (ResourceNodePlugin)SitePlugin.get().getNodePluginForNode(node);

        return plugin.getResourceManagerForMimeType(node.getMimeType());
    }

    public ResourceManagerPanel(String id, final IModel<JcrNode> nodeModel)
    {
        super(id, nodeModel);

        List<ITab> tabs = new ArrayList<ITab>();

        tabs.add(new AbstractBrixTab(new Model("View"))
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

        tabs.add(new AbstractBrixTab(new Model("Edit"))
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

        tabs.add(new AbstractBrixTab(new Model("Properties"))
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

        tabs.add(new AbstractBrixTab(new Model("Download"))
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

        add(new BrixTabbedPanel("tabbedPanel", tabs));

    }

    private boolean hasViewPermission()
    {
        Action action = new NodeActionImpl(Action.Context.ADMINISTRATION,
                NodeAction.Type.NODE_VIEW, getNode());
        return Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
    }

    private boolean hasEditPermission()
    {
        Action action = new NodeActionImpl(Action.Context.ADMINISTRATION,
                NodeAction.Type.NODE_EDIT, getNode());
        return Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
    }

}
