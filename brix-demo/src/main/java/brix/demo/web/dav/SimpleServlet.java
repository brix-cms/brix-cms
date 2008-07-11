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
import brix.demo.web.WicketApplication;
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
                Session s = (Session)request.getAttribute(key);
                if (s == null)
                {
                    s = EventUtil.wrapSession(original.getSession(request, rep, workspace));
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

	@Override
	public Repository getRepository()
	{
		WicketApplication app = (WicketApplication) Application.get("wicket.brix-demo");
		return app.getRepository();
	}

}
