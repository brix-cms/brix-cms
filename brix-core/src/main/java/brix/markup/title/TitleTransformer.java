package brix.markup.title;

import java.util.ArrayList;
import java.util.List;

import brix.Brix;
import brix.markup.MarkupSource;
import brix.markup.tag.Item;
import brix.markup.tag.Tag;
import brix.markup.transform.MarkupSourceTransformer;
import brix.plugin.site.page.AbstractContainer;

public class TitleTransformer extends MarkupSourceTransformer
{
	private final AbstractContainer container;
	
	public TitleTransformer(MarkupSource delegate, AbstractContainer container)
	{
		super(delegate);
		this.container = container;
	}

	private static final String TAG_NAME = Brix.NS_PREFIX + "title";
	
	@Override
	protected List<Item> transform(List<Item> originalItems)
	{
		List<Item> result = new ArrayList<Item>(originalItems.size());
	
		int skipLevel = 0;
		
		for (Item i : originalItems)
		{				
			if (skipLevel > 0)
			{
				if (i instanceof Tag)
				{
					Tag tag = (Tag) i;
					if (tag.getType() == Tag.Type.OPEN)
						++skipLevel;
					else if (tag.getType() == Tag.Type.CLOSE)
						--skipLevel;
						
				}
				continue;
			}
						
			if (i instanceof Tag)
			{
				Tag tag = (Tag) i;
				if (TAG_NAME.equals(tag.getName()))
				{
					result.add(new TitleText(container));
					
					if (tag.getType() == Tag.Type.OPEN)
						++skipLevel;
					
					continue;
				}
			}
			result.add(i);
		}
		
		return result;
	}

}
