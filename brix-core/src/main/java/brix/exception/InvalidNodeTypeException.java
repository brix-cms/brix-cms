package brix.exception;

public class InvalidNodeTypeException extends BrixException
{

    public InvalidNodeTypeException()
    {
    }

    public InvalidNodeTypeException(String message)
    {
        super(message);
    }

    public InvalidNodeTypeException(Throwable cause)
    {
        super(cause);
    }

    public InvalidNodeTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
