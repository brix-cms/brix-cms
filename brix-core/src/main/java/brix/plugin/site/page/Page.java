package brix.plugin.site.page;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Workspace;

import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryUtil;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;

public class Page extends AbstractContainer
{

    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {
        @Override
        public boolean canWrap(JcrNode node)
        {
            return PageSiteNodePlugin.TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(JcrNode node)
        {
            return new Page(node, node.getSession());
        }

        @Override
        public void initializeRepository(Session session)
        {
            RepositoryUtil.registerMixinType(session.getWorkspace(), PageSiteNodePlugin.TYPE,
                false, false);
        }
    };

    public Page(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static Page initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(PageSiteNodePlugin.TYPE);

        return new Page(node.getDelegate(), node.getSession());
    }
}
