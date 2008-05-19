package brix.web.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Objects;

import brix.BrixNodeModel;
import brix.BrixRequestCycle.Locator;
import brix.auth.Action;
import brix.auth.Action.Context;
import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrNodeIterator;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.auth.SiteNodeAction;


public class AbstractTreeNode implements TreeNode, IDetachable
{
    private final IModel<JcrNode> nodeModel;

    public AbstractTreeNode(IModel<JcrNode> nodeModel)
    {
        if (nodeModel == null)
        {
            throw new IllegalArgumentException("Argument 'nodeModel' may not be null.");
        }
        this.nodeModel = nodeModel;
    }

    public AbstractTreeNode(JcrNode node)
    {
        if (node == null)
        {
            throw new IllegalArgumentException("Argument 'node' may not be null.");
        }
        this.nodeModel = new BrixNodeModel(node);
    }

    public IModel<JcrNode> getNodeModel()
    {
        return nodeModel;
    }
    

    @Override
    public int hashCode()
    {
        return nodeModel.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj instanceof AbstractTreeNode == false)
            return false;
        AbstractTreeNode that = (AbstractTreeNode)obj;

        return Objects.equal(nodeModel, that.nodeModel);
    }

    private transient List<AbstractTreeNode> children;

    private void sortChildren(List<AbstractTreeNode> children)
    {
        Collections.sort(children, new Comparator<AbstractTreeNode>()
        {
            public int compare(AbstractTreeNode o1, AbstractTreeNode o2)
            {
                BrixNode n1 = (BrixNode)o1.nodeModel.getObject();
                BrixNode n2 = (BrixNode)o2.nodeModel.getObject();

                if (n1.isFolder() && !n2.isFolder())
                {
                    return -1;
                }
                else if (n2.isFolder() && !n1.isFolder())
                {
                    return 1;
                }
                return n1.getName().compareToIgnoreCase(n2.getName());
            }
        });
    }

    protected boolean displayFoldersOnly()
    {
        return true;
    }

    protected AbstractTreeNode newTreeNode(JcrNode node)
    {
        return new AbstractTreeNode(node);
    }
    
    private List<AbstractTreeNode> loadChildren()
    {
        List<AbstractTreeNode> children = new ArrayList<AbstractTreeNode>();
        JcrNodeIterator iterator = nodeModel.getObject().getNodes();
        List<JcrNode> entries = new ArrayList<JcrNode>((int)iterator.getSize());
        while (iterator.hasNext())
        {
            entries.add(iterator.nextNode());
        }

        for (JcrNode entry : entries)
        {
            BrixNode brixNode = (BrixNode)entry;
            Action view = new SiteNodeAction(Context.ADMINISTRATION, SiteNodeAction.Type.NODE_VIEW,
                    entry);
            if (!brixNode.isHidden() && (displayFoldersOnly() == false || brixNode.isFolder()) &&
                    Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(view))
            {
                children.add(newTreeNode(entry));
            }
        }

        sortChildren(children);

        return children;
    }


    public List<AbstractTreeNode> getChildren()
    {
        if (children == null)
        {
            Action viewChildren = new SiteNodeAction(Context.ADMINISTRATION,
                    SiteNodeAction.Type.NODE_VIEW_CHILDREN, nodeModel.getObject());
            if (Locator.getBrix().getAuthorizationStrategy().isActionAuthorized(viewChildren))
            {
                children = loadChildren();
            }
            else
            {
                children = Collections.emptyList();
            }
        }
        return children;
    }

    public Enumeration< ? > children()
    {
        return Collections.enumeration(getChildren());
    }

    public boolean getAllowsChildren()
    {
        return true;
    }

    public TreeNode getChildAt(int childIndex)
    {
        return getChildren().get(childIndex);
    }

    public int getChildCount()
    {
        return getChildren().size();
    }

    public int getIndex(TreeNode node)
    {
        return getChildren().indexOf(node);
    }
    
    public boolean isLeaf()
    {
        return ((BrixNode)nodeModel.getObject()).isFolder() == false;
    }
    
    public void detach()
    {
        children = null;
        nodeModel.detach();
    }
    
    public TreeNode getParent()
    {
        JcrNode node = getNodeModel().getObject();
        if (node.getDepth() == 0)
        {
            return null;
        }
        else
        {
            return newTreeNode(node.getParent());
        }
    }
    
    @Override
    public String toString()
    {
        return nodeModel.getObject().toString();
    }

}
