package brix.plugin.publishing;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import brix.BrixRequestCycle;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.plugin.publishing.auth.PublishWorkspaceAction;
import brix.plugin.site.SitePlugin;
import brix.web.admin.navigation.NavigationAwarePanel;
import brix.workspace.Workspace;

public class PublishingPanel extends NavigationAwarePanel<Workspace>
{
    public PublishingPanel(String id, IModel<Workspace> model)
    {
        super(id, model);
        
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
        }

        @Override
        public boolean isVisible()
        {
            Workspace workspace = PublishingPanel.this.getModelObject();
            String state = SitePlugin.get().getWorkspaceState(workspace);
            Action action = new PublishWorkspaceAction(Context.ADMINISTRATION, workspace,
                targetState);
            
            return requiredState.equals(state) &&
                BrixRequestCycle.Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(
                    action);
        }

    };
}
