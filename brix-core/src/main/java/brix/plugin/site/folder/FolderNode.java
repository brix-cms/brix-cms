package brix.plugin.site.folder;

import javax.jcr.Node;

import brix.jcr.JcrNodeWrapperFactory;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;
import brix.web.reference.Reference;

public class FolderNode extends BrixNode
{

    /**
     * NodeWrapperFactory that can create {@link FolderNode} wrappers
     */
    public static final JcrNodeWrapperFactory FACTORY = new JcrNodeWrapperFactory()
    {

        /** {@inheritDoc} */
        @Override
        public boolean canWrap(JcrNode node)
        {
            return node.isNodeType("nt:folder");
        }

        /** {@inheritDoc} */
        @Override
        public JcrNode wrap(Node node, JcrSession session)
        {
            return new FolderNode(node, session);
        }

    };


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

}
