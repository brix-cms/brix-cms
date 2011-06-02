/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brixcms.plugin.snapshot;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.brixcms.Brix;
import org.brixcms.Plugin;
import org.brixcms.jcr.api.JcrSession;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.tab.AbstractWorkspaceTab;
import org.brixcms.web.tab.IBrixTab;
import org.brixcms.workspace.Workspace;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnapshotPlugin implements Plugin {
    private static final String ID = SnapshotPlugin.class.getName();

    private static final String WORKSPACE_TYPE = "brix:snapshot";

    private static final String WORKSPACE_ATTRIBUTE_SITE_NAME = "brix:snapshot-site-name";

    private static final String WORKSPACE_ATTRIBUTE_CREATED = "brix:snapshot-created";

    private static final String WORKSPACE_COMMENT = "brix:snapshot-comment";

    private final Brix brix;

    public static SnapshotPlugin get() {
        return get(Brix.get());
    }

    public static SnapshotPlugin get(Brix brix) {
        return (SnapshotPlugin) brix.getPlugin(ID);
    }

    public SnapshotPlugin(Brix brix) {
        this.brix = brix;
    }


    public String getId() {
        return ID;
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        return "Snapshot - " + getSnapshotSiteName(workspace) + " - " + df.format(getCreated(workspace));
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        if (isFrontend) {
            return getSnapshotsForWorkspace(currentWorkspace);
        } else {
            return null;
        }
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {

    }

    public boolean isPluginWorkspace(Workspace workspace) {
        return isSnapshotWorkspace(workspace);
    }

    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel) {
        IBrixTab tabs[] = new IBrixTab[]{new Tab(new ResourceModel("snapshots", "Snapshots"),
                workspaceModel)};
        return Arrays.asList(tabs);
    }

    public void createSnapshot(Workspace workspace, String comment) {
        if (!SitePlugin.get().isSiteWorkspace(workspace)) {
            throw new IllegalStateException("Workspace must be a Site workspace");
        }

        Workspace targetWorkspace = brix.getWorkspaceManager().createWorkspace();
        targetWorkspace.setAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
        targetWorkspace.setAttribute(WORKSPACE_ATTRIBUTE_SITE_NAME, SitePlugin.get().getWorkspaceName(workspace));
        targetWorkspace.setAttribute(WORKSPACE_COMMENT, comment);

        setCreated(targetWorkspace, new Date());

        JcrSession originalSession = brix.getCurrentSession(workspace.getId());

        JcrSession targetSession = brix.getCurrentSession(targetWorkspace.getId());
        brix.clone(originalSession, targetSession);
    }

    public void setCreated(Workspace workspace, Date created) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        String formatted = df.format(created);
        workspace.setAttribute(WORKSPACE_ATTRIBUTE_CREATED, formatted);
    }

    /**
     * Gets the Comment for the Workspace
     *
     * @param workspace the comment holding Workspace
     * @return the comment of the Workspace as String
     */
    public String getComment(Workspace workspace) {
        return workspace.getAttribute(WORKSPACE_COMMENT);
    }

    public Date getCreated(Workspace workspace) {
        String formatted = workspace.getAttribute(WORKSPACE_ATTRIBUTE_CREATED);
        if (formatted == null) {
            return null;
        } else {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
            Date date;
            try {
                date = df.parse(formatted);
                return date;
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public List<Workspace> getSnapshotsForWorkspace(Workspace workspace) {
        String siteName = null;

        if (SitePlugin.get().isSiteWorkspace(workspace)) {
            siteName = SitePlugin.get().getWorkspaceName(workspace);
        } else if (SnapshotPlugin.get().isSnapshotWorkspace(workspace)) {
            siteName = getSnapshotSiteName(workspace);
        }

        if (siteName != null) {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put(Brix.WORKSPACE_ATTRIBUTE_TYPE, WORKSPACE_TYPE);
            attributes.put(WORKSPACE_ATTRIBUTE_SITE_NAME, siteName);
            return brix.getWorkspaceManager().getWorkspacesFiltered(attributes);
        } else {
            return Collections.emptyList();
        }
    }

    public boolean isSnapshotWorkspace(Workspace workspace) {
        return WORKSPACE_TYPE.equals(workspace.getAttribute(Brix.WORKSPACE_ATTRIBUTE_TYPE));
    }

    public String getSnapshotSiteName(Workspace workspace) {
        return workspace.getAttribute(WORKSPACE_ATTRIBUTE_SITE_NAME);
    }

    public void restoreSnapshot(Workspace snapshotWorkspace, Workspace targetWorkspace) {
        JcrSession sourceSession = brix.getCurrentSession(snapshotWorkspace.getId());
        JcrSession targetSession = brix.getCurrentSession(targetWorkspace.getId());
        brix.clone(sourceSession, targetSession);
        brix.initWorkspace(targetWorkspace, brix.getCurrentSession(targetWorkspace.getId()));
    }

    /**
     * Sets the Comment for the Workspace
     *
     * @param workspace the comment holding Workspace
     * @param comment   the comment for the Workspace
     */
    public void setComment(Workspace workspace, String comment) {
        workspace.setAttribute(WORKSPACE_COMMENT, comment);
    }

    static class Tab extends AbstractWorkspaceTab {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 49);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new ManageSnapshotsPanel(panelId, workspaceModel);
        }
    }
}
