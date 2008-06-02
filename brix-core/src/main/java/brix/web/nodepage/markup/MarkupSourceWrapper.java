package brix.web.nodepage.markup;

/**
 * Simple wrapper for {@link MarkupSource} that forwards all calls to delegate
 * instance.
 * 
 * @author Matej Knopp
 */
public class MarkupSourceWrapper implements MarkupSource
{
	private final MarkupSource delegate;

	public MarkupSourceWrapper(MarkupSource delegate)
	{
		this.delegate = delegate;
	}

	public MarkupSource getDelegate()
	{
		return delegate;
	}

	public Object getExpirationToken()
	{
		return delegate.getExpirationToken();
	}

	public boolean isMarkupExpired(Object expirationToken)
	{
		return delegate.isMarkupExpired(expirationToken);
	}

	public Item nextMarkupItem()
	{
		return delegate.nextMarkupItem();
	}

	public String getDoctype()
	{
		return delegate.getDoctype();
	}
}
