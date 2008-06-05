package brix.plugin.fragment;

import java.util.List;

import brix.Brix;
import brix.Plugin;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.admin.navigation.NavigationTreeNode;
import brix.workspace.Workspace;

public class FragmentPlugin implements Plugin
{
    private static final String FRAGMENTS_NODE_NAME = Brix.NS_PREFIX + "fragments";

    private final Brix brix;

    public FragmentPlugin(Brix brix)
    {
        super();
        this.brix = brix;
    }

    public String getId()
    {
        return FragmentPlugin.class.getName();
    }

    public String getUserVisibleName(Workspace workspace, boolean isFrontend)
    {
        return null;
    }

    public List<Workspace> getWorkspaces(Workspace currentWorkspace, boolean isFrontend)
    {
        return null;
    }

    public void initWorkspace(Workspace workspace, JcrSession workspaceSession)
    {
        JcrNode root = (JcrNode)workspaceSession.getItem(brix.getRootPath());
        JcrNode fragments;
        if (root.hasNode(FRAGMENTS_NODE_NAME))
        {
            fragments = root.getNode(FRAGMENTS_NODE_NAME);
        }
        else
        {
            fragments = root.addNode(FRAGMENTS_NODE_NAME, "nt:unstructured");
        }
        if (!fragments.isNodeType(BrixNode.JCR_TYPE_BRIX_NODE))
        {
            fragments.addMixin(BrixNode.JCR_TYPE_BRIX_NODE);
        }

    }

    public NavigationTreeNode newNavigationTreeNode(Workspace workspace)
    {

        JcrSession session = brix.getCurrentSession(workspace.getId());
        JcrNode root = (JcrNode)session.getItem(brix.getRootPath());

        String uuid = root.getNode(FRAGMENTS_NODE_NAME).getUUID();
        return new FragmentPluginNavigationTreeNode(workspace, uuid);
    }


}
