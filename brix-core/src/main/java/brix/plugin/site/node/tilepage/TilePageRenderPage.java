package brix.plugin.site.node.tilepage;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.web.nodepage.BrixNodeWebPage;
import brix.web.nodepage.BrixPageParameters;
import brix.web.nodepage.toolbar.ToolbarBehavior;

public class TilePageRenderPage extends BrixNodeWebPage
{


    public TilePageRenderPage(final IModel<JcrNode> node, BrixPageParameters pageParameters)
    {
        super(node, pageParameters);
        add(new TilePageRenderPanel("view", node, this));
        add(new ToolbarBehavior() {
            @Override
            protected String getWorkspaceName()
            {
                return node.getObject().getSession().getWorkspace().getName();
            }
        });
    }

}
