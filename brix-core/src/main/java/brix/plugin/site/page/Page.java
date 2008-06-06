package brix.plugin.site.page;

import javax.jcr.Node;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;

public class Page extends AbstractContainer
{

    public Page(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static boolean canHandle(JcrNode node)
    {
        return PageSiteNodePlugin.TYPE.equals(getNodeType(node));
    }

    public static Page initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(PageSiteNodePlugin.TYPE);

        return new Page(node.getDelegate(), node.getSession());
    }
}
