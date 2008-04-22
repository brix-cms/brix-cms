package brix.web;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.IDetachable;

import brix.BrixRequestCycle;
import brix.jcr.SessionBehavior;
import brix.jcr.api.JcrSession;


/**
 * Session manager helper for {@link RequestCycle}s that implement {@link BrixRequestCycle}.
 * Provides support for single-session per workspace per request pattern.
 * 
 * Implementors must call {@link #detach()} in {@link RequestCycle#detach()}
 * 
 * @author ivaynberg
 * 
 */
public class RequestCycleSessionManager implements IDetachable
{
    private static final long serialVersionUID = 1L;

    private transient Map<String, JcrSession> sessionMap;

    private final Credentials credentials;
    private final Repository repository;

    public RequestCycleSessionManager(Repository repository, Credentials credentials)
    {
        if (repository == null)
        {
            throw new IllegalArgumentException("repository cannot be null");
        }
        if (credentials == null)
        {
            throw new IllegalArgumentException("credentals cannot be null");
        }
        this.repository = repository;
        this.credentials = credentials;
    }

    public JcrSession getJcrSession(String workspace)
    {
        if (sessionMap == null)
        {
            sessionMap = new HashMap<String, JcrSession>();
        }

        JcrSession session = sessionMap.get(workspace);
        if (session == null)
        {
            try
            {
                SessionBehavior behavior = new SessionBehavior();
                session = JcrSession.Wrapper.wrap(repository.login(credentials, workspace),
                        behavior);

                behavior
                        .setWrapperRegistry(BrixRequestCycle.Locator.getBrix().getWrapperRegistry());
                sessionMap.put(workspace, session);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }

        }
        return session;
    }

    public void detach()
    {
        if (sessionMap != null)
        {
            for (JcrSession session : sessionMap.values())
            {
                session.logout();
            }
            sessionMap = null;
        }
    }

}
