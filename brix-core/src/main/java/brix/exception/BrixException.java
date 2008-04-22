package brix.exception;

public class BrixException extends RuntimeException
{

    public BrixException()
    {
    }

    public BrixException(String message)
    {
        super(message);
    }

    public BrixException(Throwable cause)
    {
        super(cause);
    }

    public BrixException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
