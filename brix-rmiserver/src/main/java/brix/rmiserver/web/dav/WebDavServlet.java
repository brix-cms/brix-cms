package brix.rmiserver.web.dav;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.server.CredentialsProvider;
import org.apache.jackrabbit.server.SessionProvider;
import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import brix.rmiserver.Role;
import brix.rmiserver.UserService;
import brix.rmiserver.jackrabbit.AuthorizationException;
import brix.rmiserver.jackrabbit.Authorizer;


public class WebDavServlet extends SimpleWebdavServlet
{

    private static final long serialVersionUID = 1L;

    private Repository repository;
    private CredentialsProvider credentialsProvider;
    private Authorizer authorizer;

    public WebDavServlet()
    {

    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        final ServletContext sc = config.getServletContext();
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(sc);
        if (context == null)
        {
            throw new IllegalStateException("Could not find application context");
        }

        repository = (Repository)BeanFactoryUtils.beanOfTypeIncludingAncestors(context,
                Repository.class);
        if (repository == null)
        {
            throw new IllegalStateException(
                    "Could not find JackRabbit repository in spring context");
        }


        UserService users = (UserService)BeanFactoryUtils.beanOfTypeIncludingAncestors(context,
                UserService.class);
        if (repository == null)
        {
            throw new IllegalStateException(
                    "Could not find UserService implementation in spring context");
        }

        authorizer = new Authorizer(users);
        credentialsProvider = getCredentialsProvider();

    }

// FIXME look into this
// @Override
// public synchronized SessionProvider getSessionProvider()
// {
// final SessionProvider original = super.getSessionProvider();
//
// return new SessionProvider()
// {
// public Session getSession(HttpServletRequest request, Repository rep, String workspace)
// throws LoginException, ServletException, RepositoryException
// {
//
// final String key = Brix.NS_PREFIX + "jcr-session";
// Session s = (Session)request.getAttribute(key);
// if (s == null)
// {
// s = EventUtil.wrapSession(original.getSession(request, rep, workspace));
// request.setAttribute(key, s);
// }
// return s;
// }
//
// public void releaseSession(Session session)
// {
// original.releaseSession(EventUtil.unwrapSession(session));
// }
// };
// }

    @Override
    public Repository getRepository()
    {
        return repository;
    }

    @Override
    public synchronized SessionProvider getSessionProvider()
    {
        final SessionProvider provider = super.getSessionProvider();
        return new SecureSessionProvider(provider);
    }

    /**
     * Session provider decorator that authorizes the user
     * 
     * @author ivaynberg
     * 
     */
    private final class SecureSessionProvider implements SessionProvider
    {
        private final SessionProvider delegate;

        private SecureSessionProvider(SessionProvider delegate)
        {
            this.delegate = delegate;
        }

        public Session getSession(HttpServletRequest request, Repository rep, String workspace)
                throws LoginException, ServletException, RepositoryException
        {

            Credentials creds = credentialsProvider.getCredentials(request);
            try
            {
                authorizer.authorize(creds, Role.WEBDAV);
            }
            catch (AuthorizationException e)
            {
                throw new LoginException(e.getMessage(), e);
            }
            return delegate.getSession(request, rep, workspace);
        }

        public void releaseSession(Session session)
        {
            delegate.releaseSession(session);
        }
    }
}
