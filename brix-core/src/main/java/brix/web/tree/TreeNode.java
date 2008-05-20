package brix.web.tree;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.model.IDetachable;

public interface TreeNode extends IDetachable, Serializable
{
    public List<? extends TreeNode> getChildren();
    
    public boolean isLeaf();
}
