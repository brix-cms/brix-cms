/**
 * 
 */
package brix;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;
import brix.jcr.wrapper.BrixNode;

public class BrixNodeModel implements IModel<BrixNode>
{

    private String id;
    private String workspaceName;
    private transient BrixNode node;

    public BrixNodeModel()
    {
    	this((BrixNode)null);
    }
    
    public BrixNodeModel(BrixNode node)
    {
        this.node = node;
        if (node != null)
        {
            this.id = getId(node);
            this.workspaceName = node.getSession().getWorkspace().getName();
        }
    }
    
    public BrixNodeModel(BrixNodeModel other)
    {
    	if (other == null)
    	{
    		throw new IllegalArgumentException("Argument 'other' may not be null.");
    	}
    	this.id = other.id;
    	this.workspaceName = other.workspaceName;
    	this.node = other.node;
    }

    public BrixNodeModel(String id, String workspaceName)
    {
        this.id = id;
        this.node = null;
        this.workspaceName = workspaceName;
    }

    public BrixNode getObject()
    {
        if (node == null)
        {
            node = loadNode(id);
        }
        return node;
    }

    public void setObject(BrixNode node)
    {
        if (node == null)
        {
            id = null;
            workspaceName = null;
            this.node = null;
        }
        else 
        {
            this.node = node;
            this.id = getId(node);
            this.workspaceName = node.getSession().getWorkspace().getName();
        }
    }

    public void detach()
    {
        node = null;
    }

    private BrixNode loadNode(String id)
    {
        if (id != null)
        {
            JcrSession session = Brix.get().getCurrentSession(workspaceName);
            if (id.startsWith("/"))
                return (BrixNode) session.getItem(id);
            else
                return (BrixNode) session.getNodeByUUID(id);
        }
        else
        {
            return null;
        }
    }

    private String getId(JcrNode node)
    {
        if (node.isNodeType("mix:referenceable"))
        {
            return node.getUUID();
        }
        else
        {
            return node.getPath();
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj instanceof BrixNodeModel == false)
            return false;

        BrixNodeModel that = (BrixNodeModel)obj;

        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.workspaceName, that.workspaceName);
    }

    @Override
    public int hashCode()
    {
        return (id != null ? id.hashCode() : 0) + 33 *
                (workspaceName != null ? workspaceName.hashCode() : 0);
    }
}