package brix.plugin.site.page.fragment;

import javax.jcr.Node;
import javax.jcr.Workspace;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryUtil;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.page.tile.TileContainerFacet;

public class FragmentsContainerNode extends BrixNode implements TileContainer
{

    /**
     * NodeWrapperFactory that can create {@link FragmentsContainerNode} wrappers
     */
    public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        /** {@inheritDoc} */
        @Override
        public boolean canWrap(JcrNode node)
        {
            return node.isNodeType(FragmentsContainerNode.TYPE);
        }

        /** {@inheritDoc} */
        @Override
        public JcrNode wrap(JcrNode node)
        {
            return new FragmentsContainerNode(node, node.getSession());
        }

        @Override
        public void initializeRepository(Workspace workspace)
        {
            RepositoryUtil.registerMixinType(workspace, FragmentsContainerNode.TYPE, false, false);
        }


    };


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


}
