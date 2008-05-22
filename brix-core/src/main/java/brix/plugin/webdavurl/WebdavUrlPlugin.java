package brix.plugin.webdavurl;

import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.tree.BaseTree;
import org.apache.wicket.markup.html.tree.LinkIconPanel;
import org.apache.wicket.model.Model;

import brix.Plugin;
import brix.jcr.api.JcrSession;
import brix.web.admin.navigation.AbstractNavigationTreeNode;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.web.admin.navigation.NavigationTreeNode;
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
	
	private static class Node extends AbstractNavigationTreeNode
	{

		public Node(String workspaceId) {
			super(workspaceId);
		}
		
		@Override
		public NavigationAwarePanel<?> newManagePanel(String id) {
			return new WebdavUrlPanel(id, new Model<String>(getWorkspaceId()));
		}
		
		@Override
		public String toString() {
			return "WebDAV";
		}
		
		@Override
		public Panel<?> newLinkPanel(String id, BaseTree tree) {
			return new LinkIconPanel(id, new Model<Node>(this), tree);
		}

	};

	public NavigationTreeNode newNavigationTreeNode(Workspace workspace) {
		return new Node(workspace.getId());
	}

}
