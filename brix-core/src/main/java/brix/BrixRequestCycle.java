package brix;

import org.apache.wicket.Application;

import brix.jcr.api.JcrSession;

/**
 * @deprecated use {@link Brix#getCurrentSession(String)}
 * @author igor.vaynberg
 * 
 */
public interface BrixRequestCycle 
{

    /**
     * @deprecated use {@link Brix#getCurrentSession(String)}
     */
  public JcrSession getJcrSession(String workspaceId);

  /**
   * @deprecated use Brix#get(Application)
   */
    public Brix getBrix();

    /**
     * @deprecated use {@link Brix#getCurrentSession(String)}
     * @author igor.vaynberg
     * 
     */
    public static class Locator
    {

        /**
         * @deprecated use {@link Brix#getCurrentSession(String)}
         */
        public static JcrSession getSession(String workspaceId)
        {
            return getBrix().getCurrentSession(workspaceId);
        }

        /**
         * @deprecated use Brix#get(Application)
         */
        public static Brix getBrix()
        {
            return Brix.get(Application.get());
        }
    };
}
