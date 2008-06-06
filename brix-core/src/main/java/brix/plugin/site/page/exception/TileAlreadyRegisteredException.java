package brix.plugin.site.page.exception;

import brix.exception.BrixException;

public class TileAlreadyRegisteredException extends BrixException
{

    public TileAlreadyRegisteredException()
    {
    }

    public TileAlreadyRegisteredException(String message)
    {
        super(message);
    }

    public TileAlreadyRegisteredException(Throwable cause)
    {
        super(cause);
    }

    public TileAlreadyRegisteredException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
