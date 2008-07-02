package brix.plugin.site;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.plugin.site.folder.FolderNode;

/**
 * Node that can wrap the brix:root/brix:site node
 * 
 * @author Matej Knopp
 */
public class SiteRootNode extends FolderNode 
{
	
	public SiteRootNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

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
			return new SiteRootNode(node, session);
		}
	};
}
