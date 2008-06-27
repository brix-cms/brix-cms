package brix.plugin.site.page;

import javax.jcr.Node;
import javax.jcr.Session;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.RepositoryUtil;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixFileNode;
import brix.jcr.wrapper.BrixNode;

public class PageNode extends AbstractContainer
{

    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {
        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return PageSiteNodePlugin.TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new PageNode(node, session);
        }

        @Override
        public void initializeRepository(Brix brix, Session session)
        {
            RepositoryUtil.registerMixinType(session.getWorkspace(), PageSiteNodePlugin.TYPE,
                false, false);
        }
    };

    public PageNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static PageNode initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(PageSiteNodePlugin.TYPE);

        return new PageNode(node.getDelegate(), node.getSession());
    }
    
    @Override
    public String getUserVisibleType()
    {    
    	return "Page";
    }
}
