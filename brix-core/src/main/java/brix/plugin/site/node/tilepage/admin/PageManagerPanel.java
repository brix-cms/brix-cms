package brix.plugin.site.node.tilepage.admin;

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
import brix.plugin.site.admin.NodeManagerPanel;
import brix.web.tab.AbstractBrixTab;
import brix.web.tab.BrixTab;
import brix.web.tab.BrixTabbedPanel;

public class PageManagerPanel extends NodeManagerPanel
{

    public PageManagerPanel(String id, IModel<JcrNode> nodeModel)
    {
        super(id, nodeModel);

        List<ITab<?>> tabs = new ArrayList<ITab<?>>();

        tabs.add(new AbstractBrixTab(new Model("view"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new ViewTab(panelId, getNodeModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasViewPermission();
            }

        });

        tabs.add(new AbstractBrixTab(new Model("edit"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new EditTab(panelId, getNodeModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission();
            }

        });
        tabs.add(new AbstractBrixTab(new Model("tiles"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new TilesPanel(panelId, getNodeModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission();
            }

        });

        tabs.add(new AbstractBrixTab(new Model("convert"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new ConvertTab(panelId, getNodeModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission();
            }
        });

        add(new BrixTabbedPanel("tabs", tabs));

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
