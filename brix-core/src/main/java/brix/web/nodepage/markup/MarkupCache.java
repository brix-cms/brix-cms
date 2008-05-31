package brix.web.nodepage.markup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wicket.MarkupContainer;

import brix.jcr.wrapper.BrixNode;


public class MarkupCache
{
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
