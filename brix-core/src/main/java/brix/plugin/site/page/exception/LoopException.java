package brix.plugin.site.page.exception;

import brix.exception.BrixException;

public class LoopException extends BrixException
{

    public LoopException()
    {
    }

    public LoopException(String message)
    {
        super(message);
    }

    public LoopException(Throwable cause)
    {
        super(cause);
    }

    public LoopException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
