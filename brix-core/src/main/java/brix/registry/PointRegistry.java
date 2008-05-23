package brix.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import brix.registry.Point.Multiplicity;

public class PointRegistry
{
    private final Map registrations = new HashMap();


    private <T> Collection<T> get(Point<T> point)
    {
        return (Collection<T>)registrations.get(point);
    }

    public synchronized <T> void register(Point<T> point, T extension)
    {
        Collection<T> extensions = get(point);
        if (extensions == null)
        {
            extensions = new LinkedList<T>();
            registrations.put(point, extensions);
        }

        final Multiplicity multiplicity = point.getMultiplicity();
        switch (multiplicity)
        {
            case SINGLETON :
            case SINGLETON_REQUIRED :
                extensions.clear();
                break;
            default :
                throw new IllegalStateException("Unhandled multiplicity: " + multiplicity);

        }

        extensions.add(extension);
    }

    private synchronized <T> Collection<T> lookup(Point<T> point)
    {
        Collection<T> extensions = get(point);
        if (extensions == null)
        {
            return Collections.emptySet();
        }
        else
        {
            ArrayList<T> copy = new ArrayList<T>(extensions.size());
            copy.addAll(extensions);
            return Collections.unmodifiableCollection(copy);
        }
    }

    public synchronized <T> Collection<T> lookupCollection(Point<T> point)
    {
        return lookup(point);
    }

    public synchronized <T> T lookupSingleton(Point<T> point)
    {
        Iterator<T> it = lookup(point).iterator();
        if (it.hasNext())
        {
            return it.next();
        }
        else
        {
            return null;
        }
    }

}
