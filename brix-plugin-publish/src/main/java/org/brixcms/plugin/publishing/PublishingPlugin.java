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

package org.brixcms.plugin.publishing;

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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PublishingPlugin implements Plugin {
    public static final String STATE_DEVELOPMENT = "development";
    public static final String STATE_STAGING = "staging";
    public static final String STATE_PRODUCTION = "production";

    private static String ID = PublishingPlugin.class.getName();
    private StateComparator stateComparator = new StateComparator();

    private final Brix brix;

    public static PublishingPlugin get() {
        return get(Brix.get());
    }

    public static PublishingPlugin get(Brix brix) {
        return (PublishingPlugin) brix.getPlugin(ID);
    }

    public PublishingPlugin(Brix brix) {
        this.brix = brix;
    }

    public StateComparator getStateComparator() {
        return stateComparator;
    }


    public String getId() {
        return ID;
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend) {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend) {
        return null;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession) {
    }

    public boolean isPluginWorkspace(Workspace workspace) {
        return false;
    }

    public List<IBrixTab> newTabs(IModel<Workspace> workspaceModel) {
        IBrixTab tabs[] = new IBrixTab[]{new Tab(new ResourceModel("publishing", "Publishing"),
                workspaceModel)};
        return Arrays.asList(tabs);
    }

    public void publish(Workspace workspace, String targetState) {
        if (workspace == null) {
            throw new IllegalArgumentException("Argument 'workspace' may not be null.");
        }
        if (targetState == null) {
            throw new IllegalArgumentException("Argument 'targetState' may not be null.");
        }

        SitePlugin sitePlugin = SitePlugin.get();

        if (!sitePlugin.isSiteWorkspace(workspace)) {
            throw new IllegalStateException("Workspace must be a Site workspace.");
        }
        if (targetState.equals(sitePlugin.getWorkspaceState(workspace))) {
            throw new IllegalStateException("Cannot publish workspace to same state it is already.");
        }

        String name = sitePlugin.getWorkspaceName(workspace);
        Workspace target = sitePlugin.getSiteWorkspace(name, targetState);
        if (target == null) {
            target = sitePlugin.createSite(name, targetState);
        }

        JcrSession sourceSession = brix.getCurrentSession(workspace.getId());
        JcrSession targetSession = brix.getCurrentSession(target.getId());

        brix.clone(sourceSession, targetSession);
    }

    private class StateComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            int i1 = Integer.MAX_VALUE;
            if (STATE_DEVELOPMENT.equals(o1))
                i1 = 1;
            else if (STATE_STAGING.equals(o1))
                i1 = 2;
            else if (STATE_PRODUCTION.equals(o1))
                i1 = 3;

            int i2 = Integer.MAX_VALUE;
            if (STATE_DEVELOPMENT.equals(o2))
                i2 = 1;
            else if (STATE_STAGING.equals(o2))
                i2 = 2;
            else if (STATE_PRODUCTION.equals(o2))
                i2 = 3;

            return i1 - i2;
        }
    }

    static class Tab extends AbstractWorkspaceTab {
        public Tab(IModel<String> title, IModel<Workspace> workspaceModel) {
            super(title, workspaceModel, 20);
        }

        @Override
        public Panel newPanel(String panelId, IModel<Workspace> workspaceModel) {
            return new PublishingPanel(panelId, workspaceModel);
        }
    }

    ;
}
