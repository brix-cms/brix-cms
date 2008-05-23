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
            case SINGLETON_REQUIRED :
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
            case COLLECTION_NOT_EMPTY :
                break;
            default :
                // TODO factor out this exception into its own class
                throw new IllegalArgumentException("Extension point: " + point.getUuid() +
                    " has unsupported multiplicity of: " + point.getMultiplicity() + ". Must be " +
                    Multiplicity.COLLECTION + " or " + Multiplicity.COLLECTION_NOT_EMPTY);
        }
        Collection<T> extensions = lookup(point);
        // check multiplicity contract is fulfilled
        if (extensions.isEmpty())
        {
            if (point.getMultiplicity() == Multiplicity.COLLECTION_NOT_EMPTY)
            {
                // TODO factor out this exception into its own class
                throw new IllegalStateException("Point: " + point.getUuid() +
                    " must have at least one registration");
            }
        }
        return extensions;
    }

    public synchronized <T> T lookupSingleton(Point<T> point)
    {
        // check multiplicity
        switch (point.getMultiplicity())
        {
            case SINGLETON :
            case SINGLETON_REQUIRED :
                break;
            default :
                // TODO factor out this exception into its own class
                throw new IllegalArgumentException("Extension point: " + point.getUuid() +
                    " has unsupported multiplicity of: " + point.getMultiplicity() + ". Must be " +
                    Multiplicity.SINGLETON + " or " + Multiplicity.SINGLETON_REQUIRED);
        }

        Iterator<T> it = lookup(point).iterator();

        if (it.hasNext())
        {
            return it.next();
        }
        else
        {
            // check multiplicity contract is fulfilled
            if (point.getMultiplicity() == Multiplicity.SINGLETON_REQUIRED)
            {
                // TODO factor out this exception into its own class
                throw new IllegalStateException("Point: " + point.getUuid() +
                    " must have at least one registration");
            }
            return null;
        }
    }

}
