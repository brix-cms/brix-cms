package brix.rmiserver.workspacemanager;

import java.rmi.Remote;

import brix.rmiserver.AbstractRmiExporterBean;
import brix.workspace.WorkspaceNodeTypeManager;
import brix.workspace.rmi.ServerWorkspaceNodeTypeManager;

public class WorkspaceNodeTypeManagerExporterBean extends AbstractRmiExporterBean
{
    private WorkspaceNodeTypeManager manager;

    public void setManager(WorkspaceNodeTypeManager manager)
    {
        this.manager = manager;
    }

    @Override
    protected Remote createServiceInstance()
    {
        return new ServerWorkspaceNodeTypeManager(manager);
    }

}
