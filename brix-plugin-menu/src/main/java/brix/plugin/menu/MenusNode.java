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

public class MenusNode extends BrixNode implements TreeAwareNode
{

	public MenusNode(Node delegate, JcrSession session)
	{
		super(delegate, session);
	}
	
	public JcrTreeNode getTreeNode(BrixNode node)
	{
		return new MenusTreeNode(node);
	}

	private static class MenusTreeNode extends AbstractJcrTreeNode
	{

		public MenusTreeNode(BrixNode node)
		{
			super(node);
		}		
	};
	
	@Override
	public boolean isFolder()
	{
		return true;
	}
	
	@Override
	public String getUserVisibleName()
	{
		return "Menus";
	}
	
	@Override
	public String getUserVisibleType()
	{
		return "";
	}
	
	public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        @Override
        public boolean canWrap(Brix brix, JcrNode node)
        {
            return node.getPath().equals(MenuPlugin.get(brix).getRootPath());
        }

        @Override
        public JcrNode wrap(Brix brix, Node node, JcrSession session)
        {
            return new MenusNode(node, session);
        }
    };

}
