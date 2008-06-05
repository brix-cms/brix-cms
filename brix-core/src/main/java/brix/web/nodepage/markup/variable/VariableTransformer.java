package brix.web.nodepage.markup.variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import brix.Brix;
import brix.jcr.wrapper.BrixNode;
import brix.web.nodepage.markup.ComponentTag;
import brix.web.nodepage.markup.Item;
import brix.web.nodepage.markup.MarkupSource;
import brix.web.nodepage.markup.Tag;
import brix.web.nodepage.markup.simple.SimpleTag;
import brix.web.nodepage.markup.transform.MarkupSourceTransformer;

public class VariableTransformer extends MarkupSourceTransformer
{
	private final BrixNode pageNode;

	public VariableTransformer(MarkupSource delegate, BrixNode pageNode)
	{
		super(delegate);
		this.pageNode = pageNode;
	}

	@Override
	protected List<Item> transform(List<Item> originalItems)
	{
		List<Item> result = new ArrayList<Item>(originalItems.size());
		for (Item i : result)
		{
			if (i instanceof Tag)
			{
				result.add(processTag((Tag) i));
			}
			else
			{
				result.add(i);
			}
		}
		return result;
	}

	private static final String VAR_TAG_NAME = Brix.NS_PREFIX + "var";

	private Item processTag(Tag tag)
	{
		String name = tag.getName();
		if (VAR_TAG_NAME.equals(name))
		{
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
		for (String s : attributes.values())
		{
			if (VariableTag.getKey(s) != null)
			{
				return new VariableTag(pageNode, tag);
			}
		}
		return tag;
	}

}
