package brix.jcr;

import javax.jcr.Repository;
import javax.jcr.Workspace;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.registry.ExtensionPoint;

/**
 * Produces wrappers for various {@link JcrNode}s. This factory is usually used to automatically
 * wrap a {@link JcrNode} retrieved from {@link JcrSession} with a subclass. For Example:
 * 
 * <code>
 * PersonNode node = (PersonNode)session.getRootNode().getNode(&quot;person&quot;);
 * node.setFirstName(&quot;Bob&quot;);
 * </code>
 * 
 * rather then something like this:
 * 
 * <code>
 * JcrNode node = session.getRootNode().getNode(&quot;person&quot;);
 * PersonNodeAdapter adapter = new PersonNodeAdapter(node);
 * adapter.setFirstName(&quot;Bob&quot;);
 * </code>
 * 
 * Since most of the time a node is wrapper based on its type the
 * {@link #initializeRepository(Repository)} method can be used for registering a node type.
 * 
 * @author igor.vaynberg
 * 
 */
public abstract class NodeWrapperFactory
{
    /**
     * Extension point for factory implementations
     * 
     * @author igor.vaynberg
     * 
     */
    public static final ExtensionPoint<NodeWrapperFactory> POINT = new ExtensionPoint<NodeWrapperFactory>()
    {
        public Multiplicity getMultiplicity()
        {
            return Multiplicity.COLLECTION;
        }

        public String getUuid()
        {
            return NodeWrapperFactory.class.getName();
        }

    };


    /**
     * Checks if this factory can wrap the node
     * 
     * @param node
     * @return true if this factory can wrap the node
     */
    public abstract boolean canWrap(JcrNode node);

    /**
     * Wraps the node with a subclass
     * 
     * @param node
     * @return wrapper
     */
    public abstract JcrNode wrap(JcrNode node);

    /**
     * Called when the repository is initialized. For example, this call can be used to register any
     * node types for nodes this factory can wrap.
     * 
     * @param repository
     */
    public void initializeRepository(Workspace workspace)
    {
        // noop
    }
}
