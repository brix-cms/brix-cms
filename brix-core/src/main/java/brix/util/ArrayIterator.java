package brix.util;

import java.util.Iterator;

public class ArrayIterator<T> implements Iterator<T>
{
    private int index = 0;
    private final T[] array;

    public ArrayIterator(T[] array)
    {
        super();
        this.array = array;
    }

    public boolean hasNext()
    {
        return index < array.length;
    }

    public T next()
    {
        return array[index++];
    }

    public void remove()
    {
        throw new UnsupportedOperationException();

    }

}
