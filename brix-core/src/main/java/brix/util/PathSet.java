package brix.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import brix.Path;

public class PathSet implements Set<Path>
{

    private final Set<Path> delegate;

    public PathSet()
    {
        this(new HashSet<Path>());
    }

    public PathSet(Set<Path> delegate)
    {
        this.delegate = delegate;
    }

    public void removeDescendants(Path path)
    {
        Iterator<Path> i = iterator();
        while (i.hasNext())
        {
            Path p = i.next();
            if (p.isDescendantOf(path))
            {
                i.remove();
            }
        }
    }

    public boolean removeWithDescendants(Path path)
    {
        boolean ret = remove(path);
        removeDescendants(path);
        return ret;
    }

    public boolean containsAncestor(Path path)
    {
        for (Path p : this)
        {
            if (p.isAncestorOf(path))
            {
                return true;
            }
        }
        return false;
    }

    public boolean containsParent(Path path)
    {
        for (Path p : this)
        {
            if (p.isParentOf(path))
            {
                return true;
            }
        }
        return false;
    }

    public boolean add(Path o)
    {
        return delegate.add(o);
    }

    public boolean addAll(Collection< ? extends Path> c)
    {
        return delegate.addAll(c);
    }

    public void clear()
    {
        delegate.clear();
    }

    public boolean contains(Object o)
    {
        return delegate.contains(o);
    }

    public boolean containsAll(Collection< ? > c)
    {
        return delegate.containsAll(c);
    }

    public boolean equals(Object o)
    {
        return delegate.equals(o);
    }

    public int hashCode()
    {
        return delegate.hashCode();
    }

    public boolean isEmpty()
    {
        return delegate.isEmpty();
    }

    public Iterator<Path> iterator()
    {
        return delegate.iterator();
    }

    public boolean remove(Object o)
    {
        return delegate.remove(o);
    }

    public boolean removeAll(Collection< ? > c)
    {
        return delegate.removeAll(c);
    }

    public boolean retainAll(Collection< ? > c)
    {
        return delegate.retainAll(c);
    }

    public int size()
    {
        return delegate.size();
    }

    public Object[] toArray()
    {
        return delegate.toArray();
    }

    public <T> T[] toArray(T[] a)
    {
        return delegate.toArray(a);
    }

}
