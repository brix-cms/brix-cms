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

public class TemplateNode extends AbstractContainer
{
    public static JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {
        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return TemplateSiteNodePlugin.TYPE.equals(getNodeType(node));
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new TemplateNode(node, session);
        }

        @Override
        public void initializeRepository(Brix brix, Session session)
        {
            RepositoryUtil.registerNodeType(session.getWorkspace(), TemplateSiteNodePlugin.TYPE, false, false, true);
        }
    };


    public static final String CONTENT_TAG = Brix.NS_PREFIX + "content";

    public TemplateNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    public static boolean canHandle(JcrNode node)
    {
        return TemplateSiteNodePlugin.TYPE.equals(getNodeType(node));
    }

    public static TemplateNode initialize(JcrNode node)
    {
        BrixNode brixNode = (BrixNode)node;
        BrixFileNode.initialize(node, "text/html");
        brixNode.setNodeType(TemplateSiteNodePlugin.TYPE);

        return new TemplateNode(node.getDelegate(), node.getSession());
    }
    
    @Override
    public String getUserVisibleType()
    {
    	return "Template";
    }
}
