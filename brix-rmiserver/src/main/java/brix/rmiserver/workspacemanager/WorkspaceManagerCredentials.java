package brix.rmiserver.workspacemanager;

import javax.jcr.Credentials;

public class WorkspaceManagerCredentials implements Credentials
{
    static Credentials INSTANCE = new WorkspaceManagerCredentials();

    WorkspaceManagerCredentials()
    {

    }

    public String getUserId()
    {
        return "workspacemanager";
    }
}
