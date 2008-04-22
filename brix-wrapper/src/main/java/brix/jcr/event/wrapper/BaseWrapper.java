package brix.jcr.event.wrapper;

class BaseWrapper<T>
{

    private final T delegate;
    private final SessionWrapper session;

    public BaseWrapper(T delegate, SessionWrapper session)
    {
        this.delegate = delegate;
        this.session = session;
    }

    public T getDelegate()
    {
        return delegate;
    }

    public SessionWrapper getSessionWrapper()
    {
        return session;
    }
}
