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

/**
 * Node that can wrap the brix:root/brix:site node
 * 
 * @author Matej Knopp
 */
public class WebRootNode extends FolderNode implements TreeAwareNode
{
	
	public WebRootNode(Node delegate, JcrSession session)
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
		return "Web";
	}

	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
	{

		@Override
		public boolean canWrap(Brix brix, JcrNode node)
		{
			return node.getPath().equals(SitePlugin.get(brix).getWebRootPath());
		}

		@Override
		public JcrNode wrap(Brix brix, Node node, JcrSession session)
		{
			return new WebRootNode(node, session);
		}
	};
}
