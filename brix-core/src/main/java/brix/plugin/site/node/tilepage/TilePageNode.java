package brix.plugin.site.node.tilepage;

import javax.jcr.Node;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;

public class TilePageNode extends TileContainerNode
{

    public TilePageNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static boolean canHandle(JcrNode node)
    {
        return TilePageNodePlugin.TYPE.equals(getNodeType(node));
    }

    public static TilePageNode initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(TilePageNodePlugin.TYPE);

        return new TilePageNode(node.getDelegate(), node.getSession());
    }
}
