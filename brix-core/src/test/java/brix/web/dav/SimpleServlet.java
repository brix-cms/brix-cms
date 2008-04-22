package brix.web.dav;

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
import brix.jcr.event.EventUtil;
import brix.web.admin.AdminApp;


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
        AdminApp app = (AdminApp)Application.get("wicket.brix");
        return app.getRepository();
    }

}
