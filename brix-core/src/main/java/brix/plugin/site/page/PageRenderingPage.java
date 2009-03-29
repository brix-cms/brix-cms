package brix.plugin.site.page;

import org.apache.wicket.model.IModel;

import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupSource;
import brix.markup.title.TitleTransformer;
import brix.markup.transform.HeadTransformer;
import brix.markup.variable.VariableTransformer;
import brix.markup.web.BrixMarkupNodeWebPage;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.toolbar.ToolbarBehavior;

public class PageRenderingPage extends BrixMarkupNodeWebPage
{


    public PageRenderingPage(final IModel<BrixNode> node, BrixPageParameters pageParameters)
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
    	return transform(source, (AbstractContainer) getModelObject());
    }
    
    public static MarkupSource transform(MarkupSource source, AbstractContainer container)
    {
    	source = new HeadTransformer(source);
    	source = new VariableTransformer(source, container);
    	source = new TitleTransformer(source, container);
    	return source;
    }
}
