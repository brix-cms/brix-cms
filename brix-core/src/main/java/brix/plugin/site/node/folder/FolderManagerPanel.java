package brix.plugin.site.node.folder;

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
import brix.web.tab.BrixTabbedPanel;

public class FolderManagerPanel extends NodeManagerPanel
{

    public FolderManagerPanel(String id, final IModel<JcrNode> folderModel)
    {
        super(id, folderModel);

        List<ITab> tabs = new ArrayList<ITab>(2);
        tabs.add(new AbstractBrixTab(new Model("Listing"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new ListFolderNodesTab(panelId, folderModel);
            }

            @Override
            public boolean isVisible()
            {
                Action action = new NodeActionImpl(Action.Context.ADMINISTRATION,
                    NodeAction.Type.NODE_VIEW_CHILDREN, folderModel.getObject());
                return Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
            }

        });
        tabs.add(new AbstractBrixTab(new Model("Create New"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new CreateNewNodesTab(panelId, folderModel);
            }

            @Override
            public boolean isVisible()
            {
                Action action = new NodeActionImpl(Action.Context.ADMINISTRATION,
                    NodeAction.Type.NODE_ADD_CHILD, folderModel.getObject());
                return Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
            }

        });
        tabs.add(new AbstractBrixTab(new Model("Properties"))
        {

            @Override
            public Panel getPanel(String panelId)
            {
                return new PropertiesTab(panelId, folderModel);
            }

            @Override
            public boolean isVisible()
            {
                Action action = new NodeActionImpl(Action.Context.ADMINISTRATION,
                    NodeAction.Type.NODE_EDIT, folderModel.getObject());
                return Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
            }

        });
        add(new BrixTabbedPanel("tabs", tabs));

    }

}
