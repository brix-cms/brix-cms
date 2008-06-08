package brix.markup.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.markup.MarkupSource;
import brix.markup.tag.ComponentTag;
import brix.markup.tag.Item;
import brix.markup.tag.Tag;
import brix.markup.tag.simple.SimpleTag;
import brix.markup.transform.MarkupSourceTransformer;

public class VariableTransformer extends MarkupSourceTransformer
{
	private final BrixNode pageNode;

	public VariableTransformer(MarkupSource delegate, BrixNode pageNode)
	{
		super(delegate);
		this.pageNode = pageNode;
	}
	
	int skipLevel = 0;

	@Override
	protected List<Item> transform(List<Item> originalItems)
	{
		List<Item> result = new ArrayList<Item>(originalItems.size());
		for (Item i : originalItems)
		{
			if (i instanceof Tag)
			{
				Item item = processTag((Tag) i);
				if (item != null)
				{
					result.add(item);	
				}				
			}
			else if (skipLevel == 0)
			{
				result.add(i);
			}
		}
		return result;
	}

	private static final String VAR_TAG_NAME = Brix.NS_PREFIX + "var";

	private Item processTag(Tag tag)
	{
		if (skipLevel > 0)
		{
			if (tag.getType() == Tag.Type.OPEN)
				++skipLevel;
			else if (tag.getType() == Tag.Type.CLOSE)
				--skipLevel;
			return null;
		}
		
		String name = tag.getName();
		if (VAR_TAG_NAME.equals(name))
		{
			if (tag.getType() == Tag.Type.OPEN)
			{
				++skipLevel;
			}
			String key = tag.getAttributeMap().get("key");
			return new VariableText(pageNode, key);
		}
		else if (tag.getClass().equals(SimpleTag.class))
		{
			// simple tag is guaranteed to be "static" so we will only wrap it
			// if really needed
			return processSimpleTag(tag);
		}
		else if (tag instanceof ComponentTag)
		{
			return new VariableComponentTag(pageNode, (ComponentTag) tag);
		}
		else
		{
			return new VariableTag(pageNode, tag);
		}
	}

	private Item processSimpleTag(Tag tag)
	{
		Map<String, String> attributes = tag.getAttributeMap();
		if (attributes != null)
		{
			for (String s : attributes.values())
			{
				if (VariableTag.getKey(s) != null)
				{
					return new VariableTag(pageNode, tag);
				}
			}
		}
		return tag;
	}

}
