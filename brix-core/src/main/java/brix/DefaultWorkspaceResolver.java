package brix;

import org.apache.wicket.util.string.Strings;

public class DefaultWorkspaceResolver implements WorkspaceResolver
{
    private final char separator;

    public DefaultWorkspaceResolver(char separator)
    {
        this.separator = separator;
    }

    public String getUserVisibleWorkspaceName(String workspaceId)
    {
        return workspaceId;
    }

    private String[] split(String workspaceName)
    {
        String[] res = Strings.split(workspaceName, separator);
        if (res.length != 3)
        {
            throw new IllegalStateException("Illegal workspace name " + workspaceName +
                ". Workspace name must be in form prefix" + separator + "id" + separator + "state");
        }
        return res;
    }

    public String getWorkspaceId(String workspaceName)
    {
        return split(workspaceName)[1];
    }

    public String getWorkspaceName(String prefix, String id, String state)
    {
        return prefix + separator + id + separator + state;
    }

    public String getWorkspacePrefix(String workspaceName)
    {
        return split(workspaceName)[0];
    }

    public String getWorkspaceState(String workspaceName)
    {
        return split(workspaceName)[2];
    }

    public String getWorkspaceIdFromVisibleName(String visibleWorkspaceName)
    {
        return visibleWorkspaceName;
    }
    
    public boolean isValidWorkspaceName(String workspaceName)
    {
        return (workspaceName != null) && (Strings.split(workspaceName, separator).length == 3);
    }
}
