package brix.web.nodepage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.auth.SiteNodeAction;

public class BrixNodeWebPage extends WebPage<BrixNode>
{

    public BrixNodeWebPage(IModel<BrixNode> nodeModel)
    {
        super(nodeModel);
    }
    
    public BrixNodeWebPage(IModel<BrixNode> nodeModel, BrixPageParameters pageParameters)
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
        BrixNode node = getNodeModel().getObject();
        Action action = new SiteNodeAction(Action.Context.PRESENTATION,
                SiteNodeAction.Type.NODE_VIEW, node);
        if (!Brix.get().getAuthorizationStrategy().isActionAuthorized(action))
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

    public IModel<BrixNode> getNodeModel()
    {
        return getModel();
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
