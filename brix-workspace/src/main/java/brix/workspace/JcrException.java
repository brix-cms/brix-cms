package brix.workspace;

import javax.jcr.RepositoryException;

/**
 * Generic unchecked jcr-related exception
 * 
 * @author ivaynberg
 * 
 */
public class JcrException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public JcrException(RepositoryException cause)
    {
        super("Error accessing repository", cause);
    }

}
