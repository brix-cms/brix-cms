package brix.jcr;

import javax.jcr.Session;

public interface JcrSessionFactory
{

    /**
     * Gets current session for specified workspace
     * 
     * @param workspace
     *            workspace name or <code>null</code> for default
     * @return jcr session
     */
    Session getCurrentSession(String workspace) throws CannotOpenJcrSessionException;

}
