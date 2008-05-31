package brix.web.nodepage.markup;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains list of generated markup items and expiration token.
 * 
 * TODO: Consider optimizing the list of items by grouping static items together
 * (as text).
 * 
 * @author Matej Knopp
 */
class GeneratedMarkup
{
	final List<Item> items;

	final Object expirationToken;

	/**
	 * Creates new {@link GeneratedMarkup} instance from given
	 * {@link MarkupSource}.
	 * 
	 * @param markupSource
	 */
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
