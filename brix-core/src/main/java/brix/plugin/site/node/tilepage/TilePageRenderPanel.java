package brix.plugin.site.node.tilepage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.xmlpull.v1.XmlPullParserException;

import brix.Path;
import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.auth.SiteNodeAction;
import brix.plugin.site.node.tilepage.Markup.ComponentFragment;
import brix.plugin.site.node.tilepage.Markup.Fragment;
import brix.plugin.site.node.tilepage.Markup.StaticFragment;
import brix.plugin.site.node.tilepage.exception.MarkupParseException;
import brix.web.nodepage.BrixNodeWebPage;

public class TilePageRenderPanel extends Panel<JcrNode>
{
    public TilePageRenderPanel(String id, IModel<JcrNode> nodeModel, BrixNodeWebPage page)
    {
        super(id, nodeModel);

        final JcrNode jcrNode = nodeModel.getObject();
        final JcrSession session = jcrNode.getSession();
        final TileContainerNode node = (TileContainerNode)jcrNode;
        RepeatingView repeater = new RepeatingView("chunks");
        add(repeater);

        final Markup markup = new Markup();
        try
        {
            markup.parse(session, new Path(node.getPath()));
        }
        catch (XmlPullParserException e)
        {
            throw new MarkupParseException("Could not parse markup from tile-node '" +
                    node.getPath() + "'.", e);
        }
        for (Fragment fragment : markup.getFragments())
        {
            switch (fragment.getType())
            {
                case STATIC :
                    repeater.add(new RawLabel(repeater.newChildId(), ((StaticFragment)fragment)
                            .getMarkup(session)));
                    break;
                case COMPONENT : {
                    repeater.add(((ComponentFragment)fragment).newComponent(repeater.newChildId(),
                            page, session));
                }
            }
        }
    }

    @Override
    public boolean isVisible()
    {
        JcrNode node = (JcrNode)getModelObject();
        Action action = new SiteNodeAction(Action.Context.PRESENTATION,
                SiteNodeAction.Type.NODE_VIEW, node);
        return Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(action);
    }

    private static class RawLabel extends Label
    {

        public RawLabel(String id, String label)
        {
            super(id, label);
            setEscapeModelStrings(false);
            setRenderBodyOnly(true);
        }

    }

}
