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

    /**
     * Creates a new session. Sessions returned by this method are not managed by the session
     * factory implementation, the callee is responsible for closing the session.
     * 
     * @param workspace
     * @return
     * @throws CannotOpenJcrSessionException
     */
    Session createSession(String workspace) throws CannotOpenJcrSessionException;

}
