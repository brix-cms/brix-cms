package brix.web.nodepage.markup;

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

}
