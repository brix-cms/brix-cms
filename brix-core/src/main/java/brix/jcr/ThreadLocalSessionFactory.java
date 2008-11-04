package brix.jcr;

import javax.jcr.Credentials;
import javax.jcr.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadLocalSessionFactory extends AbstractThreadLocalSessionFactory
        implements
            JcrSessionFactory
{
    static final Logger logger = LoggerFactory.getLogger(ThreadLocalSessionFactory.class);

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

    @Override
    protected Repository getRepository()
    {
        return repository;
    }

    @Override
    protected Credentials getCredentials()
    {
        return credentials;
    }

}
