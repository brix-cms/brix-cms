package brix.web.nodepage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.jcr.api.JcrNode;
import brix.plugin.site.auth.SiteNodeAction;

public class BrixNodeWebPage extends WebPage
{

    public BrixNodeWebPage(IModel<JcrNode> nodeModel)
    {
        super(nodeModel);
    }
    
    public BrixNodeWebPage(IModel<JcrNode> nodeModel, BrixPageParameters pageParameters)
    {
        super(nodeModel);
        this.pageParameters = pageParameters;
    }

    @Override
    protected void onBeforeRender()
    {
        checkAccess();
        super.onBeforeRender();
    }

    protected void checkAccess()
    {
        JcrNode node = getNodeModel().getObject();
        Action action = new SiteNodeAction(Action.Context.PRESENTATION,
                SiteNodeAction.Type.NODE_VIEW, node);
        if (!Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action))
        {
            throw new RestartResponseException(ForbiddenPage.class);
        }
    }

    public BrixPageParameters getBrixPageParameters()
    {
        if (pageParameters == null)
        {
            pageParameters = new BrixPageParameters();
        }
        return pageParameters;
    }

    public IModel<JcrNode> getNodeModel()
    {
        return (IModel<JcrNode>)(IModel)getModel();
    }

    @Override
    public boolean isBookmarkable()
    {
        return true;
    }

    private BrixPageParameters pageParameters;

    public boolean initialRedirect()
    {
        return false;
    }
}
