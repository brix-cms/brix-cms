package brix.plugin.user;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.markup.html.panel.Panel;
import brix.Plugin;
import brix.Brix;
import brix.web.tab.IBrixTab;
import brix.web.tab.AbstractWorkspaceTab;
import brix.jcr.api.JcrSession;
import brix.workspace.Workspace;

import java.util.List;
import java.util.Arrays;


/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date Mar 10, 2009 4:18:11 PM
 */
public class UserPlugin implements Plugin {
    private static String ID = UserPlugin.class.getName();

    private final Brix brix;

    public UserPlugin(Brix brix)
    {
        this.brix = brix;
    }

    public static UserPlugin get(Brix brix)
    {
        return (UserPlugin) brix.getPlugin(ID);
    }

    public static UserPlugin get()
    {
        return get(Brix.get());
    }

    public String getId()
    {
        return ID;
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return "Users";
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        return null;
    }

    public boolean isPluginWorkspace(Workspace workspace)
    {
        return false;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {
    }

    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel)
    {
        IBrixTab tabs[] = new IBrixTab[] { new Tab(new Model<String>("Users"), workspaceModel) };
        return Arrays.asList(tabs);
    }

    static class Tab extends AbstractWorkspaceTab
    {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel)
        {
            super(title, workspaceModel, 20);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
        {
            return new UserPanel(panelId, workspaceModel);
        }
    }
}
