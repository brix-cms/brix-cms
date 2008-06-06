package brix.plugin.fragment;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.TileContainerFacet;

public class FragmentsContainerNode extends BrixNode implements TileContainer
{
    public static final String TYPE = Brix.NS_PREFIX + "fragmentsContainer";

    private final TileContainerFacet manager;


    public FragmentsContainerNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
        manager = new TileContainerFacet(this);
    }


    public TileContainerFacet tiles()
    {
        return manager;
    }

    public static boolean canHandle(JcrNode node)
    {
        return node.isNodeType(FragmentsContainerNode.TYPE);
    }

}
