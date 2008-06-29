package brix.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import brix.Brix;
import brix.Plugin;
import brix.auth.Action.Context;
import brix.web.generic.BrixGenericPanel;
import brix.workspace.Workspace;
import brix.workspace.WorkspaceManager;

public class WorkspaceSwitcher extends BrixGenericPanel<Workspace>
{

	public WorkspaceSwitcher(String id, IModel<Workspace> model)
	{
		super(id, model);

		IModel<List<? extends Workspace>> workspaceModel = new LoadableDetachableModel<List<? extends Workspace>>()
		{
			@Override
			protected List<? extends Workspace> load()
			{
				return getWorkspaces();
			}
		};
		DropDownChoice<Workspace> choice = new DropDownChoice<Workspace>("workspaces", model, workspaceModel,
				new Renderer())
		{
			@Override
			protected boolean wantOnSelectionChangedNotifications()
			{
				return true;
			}

			@Override
			protected void onSelectionChanged(Workspace newSelection)
			{
				detach();
				super.onSelectionChanged(newSelection);
			}
		};
		choice.setNullValid(false);
		add(choice);

		fixCurrentWorkspace();
	}

	private boolean isCurrentWorkspaceValid()
	{
		WorkspaceManager manager = getBrix().getWorkspaceManager();
		Workspace workspace = getModelObject();
		return workspace != null && manager.workspaceExists(workspace.getId());
	}

	private void fixCurrentWorkspace()
	{
		if (!isCurrentWorkspaceValid())
		{
			List<Workspace> workspaces = getWorkspaces();
			if (!workspaces.isEmpty())
			{
				setModelObject(workspaces.iterator().next());
			}
		}
	}

	@Override
	protected void onBeforeRender()
	{
		fixCurrentWorkspace();
		super.onBeforeRender();
	}

	private Brix getBrix()
	{
		return Brix.get();
	}

	private Map<String, String> workspaceNameCache;

	private class Renderer implements IChoiceRenderer<Workspace>
	{
		public Object getDisplayValue(Workspace object)
		{
			return getWorkspaceName(object);
		}

		public String getIdValue(Workspace object, int index)
		{
			return object.getId();
		}
	};

	private String getWorkspaceName(Workspace workspace)
	{
		if (workspaceNameCache == null)
		{
			workspaceNameCache = new HashMap<String, String>();
		}
		String name = workspaceNameCache.get(workspace.getId());
		if (name == null)
		{
			for (Plugin p : getBrix().getPlugins())
			{
				if (p.isPluginWorkspace(workspace))
				{
					name = p.getUserVisibleName(workspace, false);
				}
				workspaceNameCache.put(workspace.getId(), name);
			}
		}
		return name;
	}

	@Override
	protected void onDetach()
	{
		workspaceNameCache = null;
		super.onDetach();
	}

	private List<Workspace> getWorkspaces()
	{
		Brix brix = getBrix();
		List<Workspace> workspaces = new ArrayList<Workspace>();

		Workspace current = getModelObject();

		for (Plugin p : brix.getPlugins())
		{
			List<Workspace> filtered = brix.filterVisibleWorkspaces(p.getWorkspaces(current, false),
					Context.ADMINISTRATION);
			for (Workspace w : filtered)
			{
				if (workspaceNameCache == null)
				{
					workspaceNameCache = new HashMap<String, String>();
				}
				workspaceNameCache.put(w.getId(), p.getUserVisibleName(w, false));
				workspaces.add(w);
			}
		}

		if (!workspaces.contains(current))
		{
			workspaces.add(current);
		}
		return workspaces;
	}

}
