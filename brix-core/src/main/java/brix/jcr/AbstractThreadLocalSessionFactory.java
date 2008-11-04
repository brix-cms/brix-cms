package brix.jcr;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractThreadLocalSessionFactory
{
    static final Logger logger = LoggerFactory.getLogger(AbstractThreadLocalSessionFactory.class);

    ThreadLocal<Map<String, Session>> container = new ThreadLocal<Map<String, Session>>()
    {
        @Override
        protected Map<String, Session> initialValue()
        {
            return new HashMap<String, Session>();
        }
    };

    public AbstractThreadLocalSessionFactory()
    {
        super();
    }

    protected abstract Credentials getCredentials();

    protected abstract Repository getRepository();

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
                final Credentials credentials=getCredentials();
                logger.debug("Opening managed jcr session to workspace: {} with credentials: {}",
                    workspace, credentials);
                session = getRepository().login(credentials, workspace);
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
            final Credentials credentials=getCredentials();
            logger.debug("Opening unmanaged jcr session to workspace: {} with credentials: {}",
                workspace, credentials);
            return getRepository().login(credentials, workspace);
        }
        catch (Exception e)
        {
            throw new CannotOpenJcrSessionException(workspace, e);
        }
    }

}