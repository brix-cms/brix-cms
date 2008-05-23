package brix.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
        if (multiplicity == Multiplicity.SINGLETON)
        {
            extensions.clear();
            extensions.add(extension);
        }
        else if (multiplicity == Multiplicity.COLLECTION)
        {
            extensions.add(extension);
        }
        else
        {
            throw new IllegalStateException("Unhandled multiplicity: " + multiplicity);
        }
    }

    public synchronized <T> Collection<T> lookup(Point<T> point)
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


}
