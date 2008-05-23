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
    private final Map<Point< ? >, Collection< ? >> registrations = new HashMap<Point< ? >, Collection< ? >>();


    @SuppressWarnings("unchecked")
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
                extensions.clear();
                break;

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
        // check multiplicity
        switch (point.getMultiplicity())
        {
            case COLLECTION :
                break;
            default :
                // TODO factor out this exception into its own class
                throw new IllegalArgumentException("Extension point: " + point.getUuid() +
                    " has unsupported multiplicity of: " + point.getMultiplicity() + ". Must be " +
                    Multiplicity.COLLECTION);
        }
        Collection<T> extensions = lookup(point);
        return extensions;
    }

    public synchronized <T> T lookupSingleton(Point<T> point)
    {
        // check multiplicity
        switch (point.getMultiplicity())
        {
            case SINGLETON :
                break;
            default :
                // TODO factor out this exception into its own class
                throw new IllegalArgumentException("Extension point: " + point.getUuid() +
                    " has unsupported multiplicity of: " + point.getMultiplicity() + ". Must be " +
                    Multiplicity.SINGLETON);
        }

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
