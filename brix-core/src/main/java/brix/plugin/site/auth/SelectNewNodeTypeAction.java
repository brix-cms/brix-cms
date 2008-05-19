package brix.plugin.site.auth;

import brix.auth.AbstractAction;
import brix.jcr.api.JcrNode;

/**
 * Action used to filter the list of available node types upon node creation.
 * 
 * @author Matej Knopp
 */
public class SelectNewNodeTypeAction extends AbstractAction
{
    private final JcrNode parentNode;
    private final String nodeType;

    public SelectNewNodeTypeAction(Context context, JcrNode parentNode, String nodeType)
    {
        super(context);
        this.parentNode = parentNode;
        this.nodeType = nodeType;
    }

    public String getNodeType()
    {
        return nodeType;
    }

    public JcrNode getParentNode()
    {
        return parentNode;
    }
}
