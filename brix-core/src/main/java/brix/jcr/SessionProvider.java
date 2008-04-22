package brix.jcr;

import brix.jcr.api.JcrSession;

public interface SessionProvider
{
    public JcrSession getJcrSession(String workspaceName);
}
