package brix.web.nodepage.toolbar;

import java.util.List;

public interface WorkspaceListProvider
{

    public class Entry
    {
        public String workspaceName;
        public String userVisibleName;
    }

    public List<Entry> getVisibleWorkspaces(String currentWorkspaceName);

}
