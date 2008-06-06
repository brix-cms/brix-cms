package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.markup.BrixMarkupNodeWebPage;
import brix.web.nodepage.markup.MarkupSource;
import brix.web.nodepage.markup.transform.HeadTransformer;
import brix.web.nodepage.toolbar.ToolbarBehavior;

public class PageRenderingPanel extends BrixMarkupNodeWebPage
{


    public PageRenderingPanel(final IModel<BrixNode> node, BrixPageParameters pageParameters)
    {
        super(node, pageParameters);
      //  add(new TilePageRenderPanel("view", node, this));
        add(new ToolbarBehavior() {
            @Override
            protected String getCurrentWorkspaceId()
            {
                return node.getObject().getSession().getWorkspace().getName();
            }
        });
    }

    public MarkupSource getMarkupSource()
    {
    	MarkupSource source = new PageMarkupSource((AbstractContainer)getModelObject());
    	source = new HeadTransformer(source);
    	return source;
    }
}
