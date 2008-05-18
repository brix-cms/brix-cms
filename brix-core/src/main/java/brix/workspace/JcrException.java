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

    public JcrException(RepositoryException cause)
    {
        super("Error accessing repository", cause);
    }

}
