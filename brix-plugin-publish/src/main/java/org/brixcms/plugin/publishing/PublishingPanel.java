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

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.brixcms.Brix;
import org.brixcms.auth.Action;
import org.brixcms.auth.Action.Context;
import org.brixcms.plugin.publishing.auth.PublishWorkspaceAction;
import org.brixcms.plugin.site.SitePlugin;
import org.brixcms.web.generic.BrixGenericPanel;
import org.brixcms.workspace.Workspace;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PublishingPanel extends BrixGenericPanel<Workspace> {
    public PublishingPanel(String id, IModel<Workspace> model) {
        super(id, model);

        add(new FeedbackPanel("feedback"));

        add(new PublishLink("toStaging", PublishingPlugin.STATE_DEVELOPMENT, PublishingPlugin.STATE_STAGING));
        add(new PublishLink("toProduction", PublishingPlugin.STATE_STAGING, PublishingPlugin.STATE_PRODUCTION));
    }

    private class PublishLink extends Link<Void> {
        private final String targetState;
        private final String requiredState;

        public PublishLink(String id, String requiredState, String targetState) {
            super(id);
            this.targetState = targetState;
            this.requiredState = requiredState;
        }

        @Override
        public void onClick() {
            Workspace workspace = PublishingPanel.this.getModelObject();
            PublishingPlugin.get().publish(workspace, targetState);
            Map<String, String> map = new HashMap<String, String>();
            map.put("targetState", targetState);
            getSession().info(getString("published", new Model<Serializable>((Serializable) map)));
        }

        @Override
        public boolean isVisible() {
            Workspace workspace = PublishingPanel.this.getModelObject();
            String state = SitePlugin.get().getWorkspaceState(workspace);
            Action action = new PublishWorkspaceAction(Context.ADMINISTRATION, workspace,
                    targetState);

            return requiredState.equals(state) &&
                    Brix.get().getAuthorizationStrategy().isActionAuthorized(
                            action);
        }
    }

    ;
}
