package brix.web.nodepage.markup;

import java.util.ArrayList;
import java.util.List;

class GeneratedMarkup
{
	final List<Item> items;
	
	final Object expirationToken;
	
	public GeneratedMarkup(MarkupSource markupSource)
	{
		if (markupSource == null)
		{
			throw new IllegalArgumentException("Argument 'markupSource' may not be null.");
		}
		this.expirationToken = markupSource.getExpirationToken();
		items = new ArrayList<Item>();
		Item item = markupSource.nextMarkupItem();
		while (item != null)
		{
			items.add(item);
			item = markupSource.nextMarkupItem();
		}
	}
	
}
