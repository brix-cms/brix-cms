package brix.jcr.exception;

import javax.jcr.RepositoryException;

/**
 * 
 * @author Matej Knopp
 */
public class JcrException extends RuntimeException
{

    public JcrException(RepositoryException cause)
    {
        super(cause);
    }

    public JcrException(String message, RepositoryException cause)
    {
        super(message, cause);
    }

    @Override
    public RepositoryException getCause()
    {
        return (RepositoryException)super.getCause();
    }
}
