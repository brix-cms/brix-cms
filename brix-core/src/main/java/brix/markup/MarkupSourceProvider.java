package brix.markup;

import brix.markup.web.BrixMarkupNodePanel;
import brix.markup.web.BrixMarkupNodeWebPage;

/**
 * Implemented by components that can provide {@link MarkupSource}.
 * 
 * @see BrixMarkupNodePanel
 * @see BrixMarkupNodeWebPage
 * @see MarkupCache
 * 
 * @author Matej Knopp
 */
public interface MarkupSourceProvider
{
	public MarkupSource getMarkupSource();
}
