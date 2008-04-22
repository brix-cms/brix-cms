package brix.plugin.site.node.folder;

import javax.jcr.Node;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.reference.Reference;

public class FolderNode extends BrixNode
{

    public FolderNode(Node delegate, JcrSession session)
    {
        super(delegate, session);
    }

    private static final String REDIRECT_REFERENCE = FolderNodePlugin.TYPE + "RedirectReference";

    public void setRedirectReference(Reference reference)
    {
        ensureType();
        if (reference == null)
        {
            reference = new Reference();
        }
        reference.save(this, REDIRECT_REFERENCE);
    }

    public Reference getRedirectReference()
    {
        return Reference.load(this, REDIRECT_REFERENCE);
    }

    private void ensureType()
    {
        if (!isNodeType(FolderNodePlugin.TYPE))
        {
            addMixin(FolderNodePlugin.TYPE);
        }
    }

    public static boolean canHandle(JcrNode node)
    {
        return new BrixNode(node.getDelegate(), node.getSession()).isFolder();
    }
}
