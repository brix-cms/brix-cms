package brix.util;

import java.io.IOException;

public class StringOutputStream extends java.io.OutputStream
{

    private final StringBuilder buffer;

    public StringOutputStream()
    {
        buffer = new StringBuilder();
    }

    public StringOutputStream(int size)
    {
        buffer = new StringBuilder(size);
    }

    @Override
    public void write(int b) throws IOException
    {
        buffer.append((char)b);
    }

    public String toString()
    {
        return buffer.toString();
    }

    public int length()
    {
        return buffer.length();
    }

    public void setLength(int l)
    {
        buffer.setLength(l);
    }

}
