package brix.jcr.event.wrapper;

import javax.jcr.Session;

public class WrapperAccessor
{
    public static Session wrap(Session session)
    {
        return SessionWrapper.wrap(session);
    }

    public static Session unwrap(Session session)
    {
        while (session instanceof SessionWrapper)
        {
            session = ((SessionWrapper)session).getDelegate();
        }
        return session;
    }
}
