package brix.plugin.site.node.tilepage.exception;

import brix.exception.BrixException;

public class MarkupParseException extends BrixException
{

    public MarkupParseException()
    {
    }

    public MarkupParseException(String message)
    {
        super(message);
    }

    public MarkupParseException(Throwable cause)
    {
        super(cause);
    }

    public MarkupParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
