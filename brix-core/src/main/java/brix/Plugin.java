package brix;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrSession;
import brix.registry.ExtensionPoint;
import brix.web.tab.IBrixTab;
import brix.workspace.Workspace;

/**
 * Plugin is a top-level component in Brix. Plugins can
 * <ul>
 * <li>contribute tabs to the main tabbed panel
 * <li>contribute workspaces their are responsible for to the workspace
 * switcher
 * </ul>
 * When plugin creates a workspace it is responsible for it. For such workspace
 * {@link #isPluginWorkspace(Workspace)} must return true.
 * <p>
 * TODO: Make it possible for plugins to intercept HTTP requests to serve
 * content
 * 
 * @author Matej Knopp
 */
public interface Plugin
{
	public static final ExtensionPoint<Plugin> POINT = new ExtensionPoint<Plugin>()
	{
		public Multiplicity getMultiplicity()
		{
			return Multiplicity.COLLECTION;
		}

		public String getUuid()
		{
			return Plugin.class.getName();
		}

	};

	/**
	 * Returns the plugin Id. Each plugin must have unique ID.
	 * 
	 * @return plugin Id
	 */
	String getId();

	/**
	 * Create and return list of administration tabs for this plugin. This
	 * method is invoked only once per admin panel instance. When the selected
	 * workspace changes, the workspaceModel.getObject() will return newly
	 * selected workspace, however this method will not be called again. Each
	 * panel in tab is responsible for updating itself when workspace changed.
	 * 
	 * @param workspaceModel
	 *            model providing currently selcted workspace
	 * @return list of {@link ITab} instances or null if this plugin doesn't
	 *         contribute any tabs
	 */
	List<IBrixTab> newTabs(IModel<Workspace> workspaceModel);

	/**
	 * Initializes the given workspace. If this plugin can handle the workspace
	 * (i.e. newTabs return non-empty list) the method must make sure that the
	 * workspace contains all required nodes.
	 * <p>
	 * This method is called on Brix startup for every plugin and workspace. It
	 * is also when new wokspace is created.
	 * 
	 * @param workspace
	 * @param workspaceSession
	 */
	public void initWorkspace(Workspace workspace, JcrSession workspaceSession);

	/**
	 * Returns the list of workspaces this plugin is responsible for and which
	 * should be shown in the workspace selector. The returned list can vary
	 * according to the currently selected workspace.
	 * 
	 * @param currentWorkspace
	 *            Currently selected workspace
	 * 
	 * @param isFrontend
	 *            whether the workspace selector is part of frontend or
	 *            administration interface
	 * @return
	 */
	List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend);

	/**
	 * Returns <code>true</code> if the plugin is responsible for the given
	 * workspace. workspace. E.g. for snapshot workspaces the SnapshotPlugin
	 * should return <code>true</code>, all other plugins should return
	 * <code>false</code>.
	 * <p>
	 * Returning <code>true</code> here is not the same as returning non-empty
	 * list from {@link #newTabs(IModel)}. Even if plugin contributes tabs for
	 * certain workspace, it doesn't make it responsible for it. Plugin is
	 * usually responsible only for workspace that it creates.
	 * 
	 * @param workspace
	 * @return
	 */
	public boolean isPluginWorkspace(Workspace workspace);

	/**
	 * Returns user visible name for given workspace. The name will be shown in
	 * workspace selector. This method ill only be called for workspaces
	 * returned from {@link #getWorkspaces(Workspace, boolean)} or for
	 * workspaces for which {@link #isPluginWorkspace(Workspace)} returns true.
	 * 
	 * @param workspace
	 * @param isFrontend
	 *            whether the workspace selector is part of frontend or
	 *            administration interface
	 * @return
	 */
	public String getUserVisibleName(Workspace workspace, boolean isFrontend);
}
