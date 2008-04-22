package brix.web.picker.node;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;

public class NodeTypeFilter implements NodeFilter
{

    private final String nodeTypes[];

    public NodeTypeFilter(String nodeType)
    {
        if (nodeType == null)
        {
            throw new IllegalArgumentException("Argument 'nodeType' may not be null.");
        }
        this.nodeTypes = new String[] { nodeType };
    }

    public NodeTypeFilter(String... nodeTypes)
    {
        if (nodeTypes == null)
        {
            throw new IllegalArgumentException("Argument 'nodeTypes' may not be null.");
        }
        for (String s : nodeTypes)
        {
            if (s == null)
            {
                throw new IllegalArgumentException(
                        "Argument 'nodeTypes' may not contain null value.");
            }
        }
        this.nodeTypes = nodeTypes;
    }

    public boolean isNodeAllowed(JcrNode node)
    {
        for (String type : nodeTypes)
        {
            if (type.equals(((BrixNode)node).getNodeType()))
                return true;
        }
        return false;
    }
}
