package brix.markup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import brix.markup.tag.Comment;
import brix.markup.tag.Item;
import brix.markup.tag.Tag;
import brix.markup.tag.Text;

/**
 * Renders the given list of items into an XHTML markup.
 *  
 * @author Matej Knopp
 */
public abstract class MarkupRenderer
{
	private final String doctype;
	private final List<Item> items;

	MarkupRenderer(List<Item> items, String doctype)
	{
		this.items = items;
		this.doctype = doctype;
	}

	/**
	 * Renders the items.
	 * 
	 * @return XHTML string
	 */
	public String render()
	{
		StringBuilder builder = new StringBuilder();
		
		if (doctype != null)
		{
			builder.append(doctype);
		}

		for (Item item : items)
		{
			render(item, builder);
		}

		return builder.toString();
	}

	private void render(Item item, StringBuilder builder)
	{
		if (item instanceof Tag)
		{
			render((Tag) item, builder);
		}
		else if (item instanceof Text)
		{
			render((Text) item, builder);
		}
		else if (item instanceof Comment)
		{
			render((Comment) item, builder);
		}
		else
		{
			throw new IllegalStateException("Unknown item type '" + item.getClass().getName() + "'");
		}
	}

	private void render(Tag tag, StringBuilder builder)
	{
		if (tag.getType() == Tag.Type.CLOSE)
		{
			builder.append("</");
		}
		else
		{
			builder.append("<");
		}
		builder.append(tag.getName());

		if (tag.getType() == Tag.Type.OPEN || tag.getType() == Tag.Type.OPEN_CLOSE)
		{
			Map<String, String> attributeMap = new HashMap<String, String>(tag.getAttributeMap());
			postprocessTagAttributes(tag, attributeMap);
			for (Entry<String, String> e : attributeMap.entrySet())
			{
				builder.append(" ");
				builder.append(e.getKey());
				builder.append("=\"");
				builder.append(e.getValue());
				builder.append("\"");
			}
		}

		if (tag.getType() == Tag.Type.OPEN_CLOSE)
		{
			builder.append(" /");
		}
		
		builder.append(">");
	}
	
	abstract void postprocessTagAttributes(Tag tag, Map<String, String> attributes);

	private void render(Text text, StringBuilder builder)
	{
		builder.append(text.getText());
	}

	private void render(Comment comment, StringBuilder builder)
	{
		builder.append("<!-- ");
		builder.append(comment.getText());
		builder.append(" -->");
	}
}
