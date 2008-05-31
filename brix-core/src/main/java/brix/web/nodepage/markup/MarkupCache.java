package brix.web.nodepage.markup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.MarkupContainer;

import brix.jcr.wrapper.BrixNode;

/**
 * Contains {@link GeneratedMarkup} instances associated with
 * {@link MarkupContainer}s. The {@link MarkupContainer}s must also implement
 * {@link MarkupSourceProvider} so that the cache can check if the
 * {@link GeneratedMarkup} is still valid and generate new one in case it is
 * not.
 * 
 * @author Matej Knopp
 */
public class MarkupCache
{
	/**
	 * Returns the string representation of cache key for the given container.
	 * 
	 * @param container
	 * @return
	 */
	private String getKey(MarkupContainer<BrixNode> container)
	{
		BrixNode node = container.getModelObject();
		String nodeId = "";
		if (node != null)
		{
			if (node.isNodeType("mix:referenceable"))
			{
				nodeId = node.getUUID();
			}
			else
			{
				nodeId = node.getPath();
			}
		}
		String workspace = node.getSession().getWorkspace().getName();
		return container.getClass().getName() + "-" + workspace + "-" + nodeId;
	}

	/**
	 * Returns the {@link GeneratedMarkup} instance for given container. The
	 * container must implement {@link MarkupSourceProvider}. If the
	 * {@link GeneratedMarkup} instance is expired or not found, new
	 * {@link GeneratedMarkup} instance is generated and stored in the cache.
	 * 
	 * @param container
	 * @return
	 */
	public GeneratedMarkup getMarkup(MarkupContainer<BrixNode> container)
	{
		if (!(container instanceof MarkupSourceProvider))
		{
			throw new IllegalArgumentException("Argument 'container' must implement MarkupSourceProvider");
		}
		MarkupSourceProvider provider = (MarkupSourceProvider) container;
		final String key = getKey(container);
		GeneratedMarkup markup = map.get(key);
		if (markup != null)
		{
			// check if markup is still valid
			if (provider.getMarkupSource().isMarkupExpired(markup.expirationToken))
			{
				markup = null;
			}
		}
		if (markup == null)
		{
			markup = new GeneratedMarkup(provider.getMarkupSource());
			map.put(key, markup);
		}
		return markup;
	}

	private Map<String, GeneratedMarkup> map = new ConcurrentHashMap<String, GeneratedMarkup>();

}
