/**
 * 
 */
package brix;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrSession;

public class BrixNodeModel implements IModel<JcrNode>
{

    private String id;
    private String workspaceName;
    private transient JcrNode node;

    public BrixNodeModel(JcrNode node)
    {
        this.node = node;
        if (node != null)
        {
            this.id = getId(node);
            this.workspaceName = node.getSession().getWorkspace().getName();
        }
    }

    public BrixNodeModel(String id, String workspaceName)
    {
        this.id = id;
        this.node = null;
        this.workspaceName = workspaceName;
    }

    public JcrNode getObject()
    {
        if (node == null)
        {
            node = loadNode(id);
        }
        return (JcrNode)node;
    }

    public void setObject(JcrNode node)
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

    private JcrNode loadNode(String id)
    {
        if (id != null)
        {
            JcrSession session = BrixRequestCycle.Locator.getSession(workspaceName);
            if (id.startsWith("/"))
                return (JcrNode)session.getItem(id);
            else
                return session.getNodeByUUID(id);
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