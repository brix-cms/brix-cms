package brix.markup.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import brix.markup.MarkupSource;
import brix.markup.MarkupSourceWrapper;
import brix.markup.tag.Item;

/**
 * Base class for {@link MarkupSource} transformers. This class allows to
 * interact with the list of all {@link Item}s rather than with one item at a
 * time.
 * 
 * @author Matej Knopp
 */
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

	/**
	 * Performs the actual transformation.
	 * 
	 * @param originalItems
	 * @return
	 */
	protected abstract List<Item> transform(List<Item> originalItems);
}
