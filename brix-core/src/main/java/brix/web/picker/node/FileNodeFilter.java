package brix.web.picker.node;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;

public class FileNodeFilter implements NodeFilter
{

    public boolean isNodeAllowed(JcrNode node)
    {
        return !((BrixNode)node).isFolder();
    }

    public static final FileNodeFilter INSTANCE = new FileNodeFilter();
}
