package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.auth.Action;
import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupSource;
import brix.markup.title.TitleTransformer;
import brix.markup.transform.PanelTransformer;
import brix.markup.variable.VariableTransformer;
import brix.markup.web.BrixMarkupNodePanel;
import brix.plugin.site.auth.SiteNodeAction;

public class PageRenderingPanel extends BrixMarkupNodePanel
{
    public PageRenderingPanel(String id, IModel<BrixNode> nodeModel)
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
        source = new VariableTransformer(source, getModelObject());
        source = new TitleTransformer(source, (AbstractContainer) getModelObject());
        return source;
    }

}
