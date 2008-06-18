package brix.plugin.webdavurl;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.Plugin;
import brix.auth.Action;
import brix.jcr.api.JcrSession;
import brix.web.tab.AbstractWorkspaceTab;
import brix.workspace.Workspace;

public class WebdavUrlPlugin implements Plugin
{

    public String getId()
    {
        return getClass().getName();
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        return null;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {

    }

    public boolean isPluginWorkspace(Workspace workspace)
    {
        return false;
    }

    public List<ITab> newTabs(IModel<Workspace> workspaceModel)
    {
        ITab tabs[] = new ITab[] { new Tab(new Model<String>("Webdav"), workspaceModel) };
        return Arrays.asList(tabs);
    }

    static class Tab extends AbstractWorkspaceTab
    {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel)
        {
            super(title, workspaceModel);
        }

        @Override
        public Panel< ? > newPanel(String panelId, IModel<Workspace> workspaceModel)
        {
            return new WebdavUrlPanel(panelId,
                new Model<String>(workspaceModel.getObject().getId()));
        }

        @Override
        public boolean isVisible()
        {
            final Brix brix = Brix.get();
            final Workspace workspace = getWorkspaceModel().getObject();
            final Action action = new AccessWebDavUrlPluginAction(workspace);
            final boolean granted = brix.getAuthorizationStrategy().isActionAuthorized(action);
            return granted;
        }
    };
}
