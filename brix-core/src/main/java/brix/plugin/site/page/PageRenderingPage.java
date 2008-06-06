package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.auth.SiteNodeAction;
import brix.web.nodepage.markup.BrixMarkupNodePanel;
import brix.web.nodepage.markup.MarkupSource;
import brix.web.nodepage.markup.transform.PanelTransformer;

public class PageRenderingPage extends BrixMarkupNodePanel
{
    public PageRenderingPage(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);
    }

    @Override
    public boolean isVisible()
    {
        BrixNode node = getModelObject();
        Action action = new SiteNodeAction(Action.Context.PRESENTATION,
            SiteNodeAction.Type.NODE_VIEW, node);
        return node.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
    }

    public MarkupSource getMarkupSource()
    {
        MarkupSource source = new PageMarkupSource((AbstractContainer)getModelObject());
        source = new PanelTransformer(source);
        return source;
    }

}
