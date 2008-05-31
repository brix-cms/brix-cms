package brix.web.nodepage.markup;

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
