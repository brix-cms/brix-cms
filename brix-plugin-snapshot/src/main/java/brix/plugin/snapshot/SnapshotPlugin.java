package brix.plugin.snapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.Plugin;
import brix.jcr.api.JcrSession;
import brix.plugin.site.SitePlugin;
import brix.web.tab.AbstractWorkspaceTab;
import brix.web.tab.IBrixTab;
import brix.workspace.Workspace;

public class SnapshotPlugin implements Plugin
{

	private static final String ID = SnapshotPlugin.class.getName();

	private final Brix brix;

	public SnapshotPlugin(Brix brix)
	{
		this.brix = brix;
	}

	public String getId()
	{
		return ID;
	}

	public static SnapshotPlugin get(Brix brix)
	{
		return (SnapshotPlugin) brix.getPlugin(ID);
	}

	public static SnapshotPlugin get()
	{
		return get(Brix.get());
	}

	private static final String WORKSPACE_TYPE = "brix:snapshot";

	private static final String WORKSPACE_ATTRIBUTE_SITE_NAME = "brix:snapshot-site-name";

	private static final String WORKSPACE_ATTRIBUTE_CREATED = "brix:snapshot-created";

	public boolean isSnapshotWorkspace(Workspace workspace)
	{
		return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
	}

	public String getSnapshotSiteName(Workspace workspace)
	{
		return workspace.getAttribute(WORKSPACE_ATTRIBUTE_SITE_NAME);
	}

	public void setCreated(Workspace workspace, Date created)
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		String formatted = df.format(created);
		workspace.setAttribute(WORKSPACE_ATTRIBUTE_CREATED, formatted);
	}

	public Date getCreated(Workspace workspace)
	{
		String formatted = workspace.getAttribute(WORKSPACE_ATTRIBUTE_CREATED);
		if (formatted == null)
		{
			return null;
		}
		else
		{
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
			Date date;
			try
			{
				date = df.parse(formatted);
				return date;
			}
			catch (ParseException e)
			{
				return null;
			}
		}
	}

	public List<Workspace> getSnapshotsForWorkspace(Workspace workspace)
	{
		String siteName = null;

		if (SitePlugin.get().isSiteWorkspace(workspace))
		{
			siteName = SitePlugin.get().getWorkspaceName(workspace);
		}
		else if (SnapshotPlugin.get().isSnapshotWorkspace(workspace))
		{
			siteName = getSnapshotSiteName(workspace);
		}

		if (siteName != null)
		{
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
			attributes.put(WORKSPACE_ATTRIBUTE_SITE_NAME, siteName);
			return brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
		}
		else
		{
			return Collections.emptyList();
		}
	}

	public void createSnapshot(Workspace workspace)
	{
		if (!SitePlugin.get().isSiteWorkspace(workspace))
		{
			throw new IllegalStateException("Workspace must be a Site workspace");
		}

		Workspace targetWorkspace = brix.getWorkspaceManager().createWorkspace();
		targetWorkspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
		targetWorkspace.setAttribute(WORKSPACE_ATTRIBUTE_SITE_NAME, SitePlugin.get().getWorkspaceName(workspace));

		setCreated(targetWorkspace, new Date());

		JcrSession originalSession = brix.getCurrentSession(workspace.getId());
		;
		JcrSession targetSession = brix.getCurrentSession(targetWorkspace.getId());
		brix.clone(originalSession, targetSession);
	}

	public void restoreSnapshot(Workspace snapshotWorkspace, Workspace targetWorkspace)
	{
		JcrSession sourceSession = brix.getCurrentSession(snapshotWorkspace.getId());
		JcrSession targetSession = brix.getCurrentSession(targetWorkspace.getId());
		brix.clone(sourceSession, targetSession);
		brix.initWorkspace(targetWorkspace, brix.getCurrentSession(targetWorkspace.getId()));
	}

	public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel)
    {
		IBrixTab tabs[] = new IBrixTab[] { new Tab(new Model<String>("Snapshots"), workspaceModel) };
    	return Arrays.asList(tabs);
    }

	static class Tab extends AbstractWorkspaceTab
	{
		public Tab(IModel<String> title, IModel<Workspace> workspaceModel)
		{
			super(title, workspaceModel, 49);
		}

		@Override
		public Panel newPanel(String panelId, IModel<Workspace> workspaceModel)
		{
			return new ManageSnapshotsPanel(panelId, workspaceModel);
		}
	};

	public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
	{

	}

	public boolean isPluginWorkspace(Workspace workspace)
	{
		return isSnapshotWorkspace(workspace);
	}

	public String getUserVisibleName(Workspace workspace, boolean isFrontend)
	{
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		return "Snapshot - " + getSnapshotSiteName(workspace) + " - " + df.format(getCreated(workspace));
	}

	public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
	{
		if (isFrontend)
		{
			return getSnapshotsForWorkspace(currentWorkspace);
		}
		else
		{
			return null;
		}
	}
}
