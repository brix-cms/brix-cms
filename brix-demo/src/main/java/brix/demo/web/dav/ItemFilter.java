package brix.demo.web.dav;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.webdav.simple.DefaultItemFilter;

import brix.jcr.wrapper.BrixNode;

public class ItemFilter extends DefaultItemFilter
{

	public ItemFilter()
	{

	}

	@Override
	public boolean isFilteredItem(Item item)
	{
		try
		{
			if (item instanceof Node)
			{
				Node node = (Node) item;
				if (node.isNodeType(BrixNode.JCR_MIXIN_BRIX_HIDDEN))
				{
					return true;
				}
			}
			else
			{
				String name = item.getName();
				if (name.startsWith("brix:"))
				{
					return true;
				}
			}
		}
		catch (RepositoryException e)
		{
			return true;
		}

		return super.isFilteredItem(item);
	}

}
