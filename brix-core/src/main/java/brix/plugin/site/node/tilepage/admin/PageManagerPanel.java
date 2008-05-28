package brix.plugin.site.node.tilepage.admin;

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
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.admin.NodeManagerPanel;
import brix.plugin.site.auth.SiteNodeAction;

public class PageManagerPanel extends NodeManagerPanel
{

    public PageManagerPanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);

        List<ITab> tabs = new ArrayList<ITab>();

        tabs.add(new AbstractTab(new Model("view"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new ViewTab(panelId, PageManagerPanel.this.getModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasViewPermission();
            }

        });

        tabs.add(new AbstractTab(new Model("edit"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new EditTab(panelId, PageManagerPanel.this.getModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission();
            }

        });
        tabs.add(new AbstractTab(new Model("tiles"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new TilesPanel(panelId, PageManagerPanel.this.getModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission();
            }

        });

        tabs.add(new AbstractTab(new Model("convert"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new ConvertTab(panelId, PageManagerPanel.this.getModel());
            }

            @Override
            public boolean isVisible()
            {
                return hasEditPermission();
            }
        });

        add(new TabbedPanel("tabs", tabs));

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
