package brix.plugin.site.page;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;

public class Template extends AbstractContainer
{

    public static final String CONTENT_TAG = Brix.NS_PREFIX + "content";

    public Template(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static boolean canHandle(JcrNode node)
    {
        return TemplateSiteNodePlugin.TYPE.equals(getNodeType(node));
    }

    public static Template initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(TemplateSiteNodePlugin.TYPE);

        return new Template(node.getDelegate(), node.getSession());
    }
}
