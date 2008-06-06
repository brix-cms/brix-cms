package brix.plugin.site.page.exception;

import brix.exception.BrixException;

public class UnknownTagException extends BrixException
{

    public UnknownTagException()
    {
    }

    public UnknownTagException(String message)
    {
        super(message);
    }

    public UnknownTagException(Throwable cause)
    {
        super(cause);
    }

    public UnknownTagException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
