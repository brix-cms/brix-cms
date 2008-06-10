package brix.plugin.webdavurl;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Plugin;
import brix.jcr.api.JcrSession;
import brix.web.tab.AbstractWorkspaceTab;
import brix.workspace.Workspace;

public class WebdavUrlPlugin implements Plugin {

	public String getId() {
		return getClass().getName();
	}

	public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
		return null;
	}

	public List<Workspace> getWorkspaces(Workspace currentWorkspace,
			boolean isFrontend) {
		return null;
	}

	public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {

	}
	
	public ITab newTab(final Workspace workspace)
	{
    	return new Tab(new Model<String>("Webdav"), workspace);
	}
	
	static class Tab extends AbstractWorkspaceTab
	{
		public Tab(IModel<String> title, Workspace workspace)
		{
			super(title, workspace);
		}

		@Override
		public Panel<?> newPanel(String panelId, IModel<Workspace> workspaceModel)
		{
			return new WebdavUrlPanel(panelId, new Model<String>(workspaceModel.getObject().getId()));
		}
	};
}
