package brix.jcr;

public class CannotOpenJcrSessionException extends RuntimeException
{
    public CannotOpenJcrSessionException(String workspace, Exception cause)
    {
        super("Could not open jcr session for workspace: " + workspace, cause);
    }
}
