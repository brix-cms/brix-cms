package brix.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class StringInputStream extends InputStream
{

    private StringReader in;

    public StringInputStream(String source)
    {
        in = new StringReader(source);
    }

    public int read() throws IOException
    {
        return in.read();
    }

    public void close() throws IOException
    {
        in.close();
    }

    public synchronized void mark(final int limit)
    {
        try
        {
            in.mark(limit);
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe.getMessage());
        }
    }

    public synchronized void reset() throws IOException
    {
        in.reset();
    }

    public boolean markSupported()
    {
        return in.markSupported();
    }
}
