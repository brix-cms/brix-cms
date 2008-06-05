package brix.plugin.site.node.tilepage;

import org.apache.wicket.model.IModel;

import brix.Brix;
import brix.auth.Action;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.node.tilepage.markup.TilePageMarkupSource;
import brix.web.nodepage.markup.BrixMarkupNodePanel;
import brix.web.nodepage.markup.MarkupSource;
import brix.web.nodepage.markup.transform.PanelTransformer;

public class TilePageRenderPanel extends BrixMarkupNodePanel
{
    public TilePageRenderPanel(String id, IModel<BrixNode> nodeModel)
    {
        super(id, nodeModel);
    }

    @Override
    public boolean isVisible()
    {
        JcrNode node = (JcrNode)getModelObject();
        Action action = new SiteNodeAction(Action.Context.PRESENTATION,
                SiteNodeAction.Type.NODE_VIEW, node);
        return Brix.get().getAuthorizationStrategy().isActionAuthorized(action);
    }

    public MarkupSource getMarkupSource()
    {
    	MarkupSource source = new TilePageMarkupSource((TileContainerNode)getModelObject());
    	source = new PanelTransformer(source);
    	return source;
    }

}
