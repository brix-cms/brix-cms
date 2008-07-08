package brix.plugin.menu;

import javax.jcr.Node;

import brix.Brix;
import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.picker.common.TreeAwareNode;
import brix.web.tree.AbstractJcrTreeNode;
import brix.web.tree.JcrTreeNode;

public class MenuNode extends BrixNode implements TreeAwareNode
{

	public MenuNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}

	@Override
	public String getUserVisibleName()
	{
		if (hasProperty("name"))
		{
			return getProperty("name").getString();
		}
		else
		{
			return super.getUserVisibleName();
		}
	}
	
	@Override
	public String getUserVisibleType()
	{
		return "Menu";
	}
	
	public JcrTreeNode getTreeNode(BrixNode node)
	{
		return new MenuTreeNode(node);
	}

	private static class MenuTreeNode extends AbstractJcrTreeNode
	{

		public MenuTreeNode(BrixNode node)
		{
			super(node);
		}		
	};
	
	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return node.getDepth() > 1 && node.getParent().getPath().equals(MenuPlugin.get(brix).getRootPath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new MenuNode(node, session);
        }
    };

}
