package brix.plugin.publishing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.Brix;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.plugin.publishing.auth.PublishWorkspaceAction;
import brix.plugin.site.SitePlugin;
import brix.web.generic.BrixGenericPanel;
import brix.workspace.Workspace;

public class PublishingPanel extends BrixGenericPanel<Workspace>
{
    public PublishingPanel(String id, IModel<Workspace> model)
    {
        super(id, model);
        
        add(new FeedbackPanel("feedback"));
        
        add(new PublishLink("toStaging", PublishingPlugin.STATE_DEVELOPMENT, PublishingPlugin.STATE_STAGING));
        add(new PublishLink("toProduction", PublishingPlugin.STATE_STAGING, PublishingPlugin.STATE_PRODUCTION));
    }

    private class PublishLink extends Link<Void>
    {
        private final String targetState;
        private final String requiredState;

        public PublishLink(String id, String requiredState, String targetState)
        {
            super(id);
            this.targetState = targetState;
            this.requiredState = requiredState;
        }

        @Override
        public void onClick()
        {
            Workspace workspace = PublishingPanel.this.getModelObject();
            PublishingPlugin.get().publish(workspace, targetState);
            Map<String, String> map = new HashMap<String, String>();
            map.put("targetState", targetState);
            getSession().info(getString("published", new Model<Serializable>((Serializable) map)));
        }

        @Override
        public boolean isVisible()
        {
            Workspace workspace = PublishingPanel.this.getModelObject();
            String state = SitePlugin.get().getWorkspaceState(workspace);
            Action action = new PublishWorkspaceAction(Context.ADMINISTRATION, workspace,
                targetState);
            
            return requiredState.equals(state) &&
                Brix.get().getAuthorizationStrategy().isActionAuthorized(
                    action);
        }

    };
}
