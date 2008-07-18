package brix.demo.web.dav;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.apache.wicket.Application;

import brix.Brix;
import brix.Plugin;
import brix.SessionAwarePlugin;
import brix.demo.web.WicketApplication;
import brix.jcr.base.BrixSession;
import brix.jcr.base.EventUtil;

public class SimpleServlet extends SimpleWebdavServlet
{

	public SimpleServlet()
	{

	}

	@Override
    public synchronized SessionProvider getSessionProvider()
    {
        final SessionProvider original = super.getSessionProvider();

        return new SessionProvider()
        {
            public Session getSession(HttpServletRequest request, Repository rep, String workspace)
                    throws LoginException, ServletException, RepositoryException
            {

                final String key = Brix.NS_PREFIX + "jcr-session";
                BrixSession s = (BrixSession)request.getAttribute(key);
                if (s == null)
                {
                    s = EventUtil.wrapSession(original.getSession(request, rep, workspace));
                    for (Plugin p : getBrix().getPlugins())
                    {
                    	if (p instanceof SessionAwarePlugin)
                    	{
                    		((SessionAwarePlugin) p).onWebDavSession(s);
                    	}
                    }
                    request.setAttribute(key, s);                    
                }                
                return s;
            }

            public void releaseSession(Session session)
            {
                original.releaseSession(EventUtil.unwrapSession(session));
            }
        };
    }	
	
	private Brix getBrix()
	{
		WicketApplication app = (WicketApplication) Application.get("wicket.brix-demo");
		return app.getBrix();
	}
	
	@Override
	public Repository getRepository()
	{
		WicketApplication app = (WicketApplication) Application.get("wicket.brix-demo");
		return app.getRepository();
	}

}
