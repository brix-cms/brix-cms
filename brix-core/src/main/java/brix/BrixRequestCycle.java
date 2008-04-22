package brix;

import org.apache.wicket.RequestCycle;

import brix.jcr.SessionProvider;
import brix.jcr.api.JcrSession;

public interface BrixRequestCycle extends SessionProvider
{

    public JcrSession getJcrSession(String workspaceName);

    public Brix getBrix();

    public static class Locator
    {
        public static SessionProvider getSessionProvider()
        {
            return (BrixRequestCycle)RequestCycle.get();
        }

        public static JcrSession getSession(String workspaceName)
        {
            return ((BrixRequestCycle)RequestCycle.get()).getJcrSession(workspaceName);
        }

        public static Brix getBrix()
        {
            return ((BrixRequestCycle)RequestCycle.get()).getBrix();
        }
    };
}
