package brix.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import brix.registry.ExtensionPoint;

/**
 * A repository initializer. Each initializer runs once upon system startup.
 * 
 * @author igor.vaynberg
 * 
 */
public interface RepositoryInitializer
{
    /**
     * Extension point used to register repository initializers
     */
    public static final ExtensionPoint<RepositoryInitializer> POINT = new ExtensionPoint<RepositoryInitializer>()
    {

        public Multiplicity getMultiplicity()
        {
            return Multiplicity.COLLECTION;
        }

        public String getUuid()
        {
            return RepositoryInitializer.class.getName();
        }

    };


    /**
     * Performs repository initialization.
     * 
     * @param session
     *            session into the default repository workspace
     */
    public void initializeRepository(Session session) throws RepositoryException;
}
