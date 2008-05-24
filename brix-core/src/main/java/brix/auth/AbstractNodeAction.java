package brix.auth;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.workspace.Workspace;


public abstract class AbstractNodeAction extends AbstractAction
{
    private final JcrNode node;
    
    public AbstractNodeAction(Context context, JcrNode node)
    {
        super(context);
        this.node = node;
    }
    
    public JcrNode getNode()
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
            return Brix.get().getWorkspaceManager().getWorkspace(id);
        }
    }

}
