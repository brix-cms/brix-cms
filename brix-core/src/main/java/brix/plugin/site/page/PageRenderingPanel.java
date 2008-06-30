package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.auth.Action.Context;
import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupSource;
import brix.markup.title.TitleTransformer;
import brix.markup.transform.PanelTransformer;
import brix.markup.variable.VariableTransformer;
import brix.markup.web.BrixMarkupNodePanel;
import brix.plugin.site.SitePlugin;

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
        return SitePlugin.get().canViewNode(node, Context.PRESENTATION);
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
