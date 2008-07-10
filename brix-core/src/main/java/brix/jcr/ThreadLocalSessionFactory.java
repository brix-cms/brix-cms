package brix.jcr;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalSessionFactory implements JcrSessionFactory
{
    private static final Logger logger = LoggerFactory.getLogger(ThreadLocalSessionFactory.class);

    private ThreadLocal<Map<String, Session>> container = new ThreadLocal<Map<String, Session>>()
    {
        @Override
        protected Map<String, Session> initialValue()
        {
            return new HashMap<String, Session>();
        }
    };

    private final Repository repository;
    private final Credentials credentials;


    public ThreadLocalSessionFactory(Repository repository, Credentials credentials)
    {
        if (repository == null)
        {
            throw new IllegalArgumentException("repository cannot be null");
        }

        if (credentials == null)
        {
            throw new IllegalArgumentException("credentials cannot be null");
        }
        this.credentials = credentials;
        this.repository = repository;
    }

    public Session getCurrentSession(String workspace)
    {
        final Map<String, Session> map = container.get();
        Session session = map.get(workspace);
        if (session != null && !session.isLive())
        {
            session = null;
        }
        if (session == null)
        {
            try
            {
                logger.debug("Opening managed jcr session to workspace: {} with credentials: {}",
                    workspace, credentials);
                session = repository.login(credentials, workspace);
            }
            catch (Exception e)
            {
                throw new CannotOpenJcrSessionException(workspace, e);
            }
            map.put(workspace, session);
            container.set(map);
        }
        return session;
    }

    public void cleanup()
    {
        for (Session session : container.get().values())
        {
            if (session.isLive())
            {
                session.logout();
            }
        }
    }

    public Session createSession(String workspace) throws CannotOpenJcrSessionException
    {
        try
        {
            logger.debug("Opening unmanaged jcr session to workspace: {} with credentials: {}",
                workspace, credentials);
            return repository.login(credentials, workspace);
        }
        catch (Exception e)
        {
            throw new CannotOpenJcrSessionException(workspace, e);
        }
    }

}
