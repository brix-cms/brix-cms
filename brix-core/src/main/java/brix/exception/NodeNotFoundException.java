package brix.exception;

public class NodeNotFoundException extends BrixException
{

    public NodeNotFoundException()
    {
    }

    public NodeNotFoundException(String message)
    {
        super(message);
    }

    public NodeNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public NodeNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
