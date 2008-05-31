package brix.web.nodepage.markup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PanelTransformer extends HeadTransformer
{
	public PanelTransformer(MarkupSource delegate)
	{
		super(delegate);
	}

	private boolean shouldFilter(String tagName)
	{
		return "html".equals(tagName) || "body".equals(tagName) || "wicket:panel".equals(tagName);
	}

	private List<Item> filter(List<Item> items)
	{
		List<Item> result = new ArrayList<Item>();

		for (Item i : items)
		{
			if (i instanceof Tag)
			{
				Tag tag = (Tag) i;
				if (shouldFilter(tag.getName()))
				{
					continue;
				}
			}
			result.add(i);
		}

		return result;
	}

	@Override
	protected List<Item> transform(List<Item> originalItems)
	{
		List<Item> headContent = extractHeadContent(originalItems);
		List<Item> body = filter(transform(originalItems, null));

		Map<String, String> emptyMap = Collections.emptyMap();
		List<Item> result = new ArrayList<Item>();
		
		result.add(new SimpleTag("wicket:head", Tag.Type.OPEN, emptyMap));
		result.addAll(headContent);
		result.add(new SimpleTag("wicket:head", Tag.Type.CLOSE, emptyMap));
		
		result.add(new SimpleTag("wicket:panel", Tag.Type.OPEN, emptyMap));
		result.addAll(body);
		result.add(new SimpleTag("wicket:panel", Tag.Type.CLOSE, emptyMap));
		
		return result;
	}
}
