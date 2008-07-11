package brix.jcr.base.wrapper;

import javax.jcr.Session;

import brix.jcr.base.BrixSession;

public class WrapperAccessor
{
    public static BrixSession wrap(Session session)
    {
    	if (session instanceof SessionWrapper)
    	{
    		return (SessionWrapper)session;
    	}
    	else
    	{
    		return SessionWrapper.wrap(session);	
    	}        
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
