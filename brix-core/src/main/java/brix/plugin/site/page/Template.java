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

public class Template extends AbstractContainer
{
    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {
        @Override
        public boolean canWrap(JcrNode node)
        {
            return TemplateSiteNodePlugin.TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Node node, JcrSession session)
        {
            return new Template(node, session);
        }

        @Override
        public void initializeRepository(Session session)
        {
            RepositoryUtil.registerMixinType(session.getWorkspace(), TemplateSiteNodePlugin.TYPE, false, false);
        }
    };


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
