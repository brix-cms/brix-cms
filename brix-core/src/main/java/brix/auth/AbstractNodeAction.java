package brix.auth;

import brix.jcr.wrapper.BrixNode;
import brix.workspace.Workspace;


public abstract class AbstractNodeAction extends AbstractAction
{
    private final BrixNode node;
    
    public AbstractNodeAction(Context context, BrixNode node)
    {
        super(context);
        this.node = node;
    }
    
    public BrixNode getNode()
    {
        return node;
    }
    
    public Workspace getWorkspace()
    {
        if (node == null)
        {
            return null;
        }
        else
        {
            String id = node.getSession().getWorkspace().getName();            
            return node.getBrix().getWorkspaceManager().getWorkspace(id);
        }
    }

}
