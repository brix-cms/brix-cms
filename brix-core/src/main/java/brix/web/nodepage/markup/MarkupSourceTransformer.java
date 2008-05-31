package brix.web.nodepage.markup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class MarkupSourceTransformer extends MarkupSourceWrapper
{

	public MarkupSourceTransformer(MarkupSource delegate)
	{
		super(delegate);
	}
	 
	private List<Item> items = null;
	private Iterator<Item> iterator = null;
	
	@Override
	public Item nextMarkupItem()
	{
		if (items == null)
		{
			List<Item> temp = new ArrayList<Item>();
			Item i = getDelegate().nextMarkupItem();
			while (i != null)
			{
				temp.add(i);
				i = getDelegate().nextMarkupItem();
			}
			items = transform(temp);
			
			if (items == null)
			{
				items = Collections.emptyList();
			}
			iterator = items.iterator();
		}
		
		if (iterator != null && iterator.hasNext())
		{
			return iterator.next();
		}
		else
		{
			return null;
		}		
	}
	
	protected abstract List<Item> transform(List<Item> originalItems);
}
