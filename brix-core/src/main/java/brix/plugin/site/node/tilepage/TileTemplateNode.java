package brix.plugin.site.node.tilepage;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;

public class TileTemplateNode extends TileContainerNode
{

    public static final String CONTENT_TAG = Brix.NS_PREFIX + "content";

    public TileTemplateNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static boolean canHandle(JcrNode node)
    {
        return TileTemplateNodePlugin.TYPE.equals(getNodeType(node));
    }

    public static TileTemplateNode initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(TileTemplateNodePlugin.TYPE);

        return new TileTemplateNode(node.getDelegate(), node.getSession());
    }
}
