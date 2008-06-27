package brix.plugin.site;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.folder.FolderNode;
import brix.web.picker.common.TreeAwareNode;
import brix.web.tree.AbstractJcrTreeNode;
import brix.web.tree.JcrTreeNode;

public class SiteNode extends FolderNode implements TreeAwareNode
{

	public SiteNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}
	
	public JcrTreeNode getTreeNode(BrixNode node)
	{
		return new SiteTreeNode(node);
	}
	
	private static class SiteTreeNode extends AbstractJcrTreeNode
	{

		public SiteTreeNode(BrixNode node)
		{
			super(node);
		}
		
	};

	@Override
	public String getUserVisibleName()
	{
		return "Site";
	}
	
	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return node.getPath().equals(SitePlugin.get(brix).getSiteRootPath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new SiteNode(node, session);
        }
    };
}
