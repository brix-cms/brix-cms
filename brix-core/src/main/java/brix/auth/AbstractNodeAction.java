package brix.auth;

import brix.jcr.api.JcrNode;


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

}
