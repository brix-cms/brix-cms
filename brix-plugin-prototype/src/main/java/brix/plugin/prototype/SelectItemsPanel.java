package brix.plugin.prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;
import brix.web.picker.common.NodePickerTreeGridPanel;
import brix.web.picker.common.RootTreeNode;
import brix.web.tree.JcrTreeNode;

import com.inmethod.grid.treegrid.TreeGrid;

public class SelectItemsPanel<T> extends BrixGenericPanel<T>
{

    public SelectItemsPanel(String id, IModel<T> model, String workspaceId)
    {
        super(id, model);
        init(workspaceId);
    }
    
    public SelectItemsPanel(String id, String workspaceName)
    {
        super(id);
        init(workspaceName);
    }
    
    
    private NodePickerTreeGridPanel treeGrid;
    
    private void init(final String workspaceId)
    {
    	treeGrid = new NodePickerTreeGridPanel("grid", null, null)
    	{
    		@Override
    		protected JcrTreeNode getRootNode()
    		{
    			return new RootTreeNode(workspaceId);
    		}
    		@Override
    		protected void configureGrid(TreeGrid grid)
    		{
    			super.configureGrid(grid);
    			grid.setAllowSelectMultiple(true);
    	        grid.getTree().setRootLess(true);
    		}
    	};
               
        add(treeGrid);
    }

    public TreeGrid getTreeGrid()
    {
        return treeGrid.getGrid();
    }
    
    private String getNodePath(BrixNode node)
    {
    	List<BrixNode> path = new ArrayList<BrixNode>();
    	while (node.getDepth() > 1)
    	{
    		path.add(0, node);
    		node = (BrixNode) node.getParent();
    	}
    	StringBuilder res = new StringBuilder();
    	for (BrixNode n : path)
    	{
   			res.append("/");   		
    		res.append(n.getUserVisibleName());
    		if (!n.isFolder())
    		{
    			break;
    		}
    	}
        return res.toString();
    }
    
    protected String getDependenciesMessage(Map<JcrNode, List<JcrNode>> dependencies)
    {
        StringBuilder b = new StringBuilder();
        
        b.append(getString("followingDependenciesAreNotSatisfied") + "\n");
        
        for (Entry<JcrNode, List<JcrNode>> entry : dependencies.entrySet())
        {
            b.append(getNodePath((BrixNode) entry.getKey()));
            b.append(" -> ");
            
            if (entry.getValue().size() == 1)
            {
                b.append(getNodePath((BrixNode) entry.getValue().iterator().next()));
            }
            else
            {
                b.append("(");
                boolean first = true;
                
                for (JcrNode node : entry.getValue())
                {
                    if (first)
                    {
                        first = false;
                    }
                    else
                    {
                        b.append(", ");
                    }
                    b.append(getNodePath((BrixNode) node));
                }
                
                b.append(")");
            }
            
            b.append("\n");
        }
        return b.toString();
    }

    @SuppressWarnings("unchecked")
    protected List<JcrNode> getSelectedNodes()
    {
        List<JcrNode> nodes = new ArrayList<JcrNode>();
        for (IModel model : getTreeGrid().getSelectedItems())
        {
            JcrTreeNode treeNode = (JcrTreeNode)model.getObject();
            JcrNode node = treeNode.getNodeModel() != null ? treeNode.getNodeModel().getObject() : null;
            if (node != null)
            	nodes.add(node);
        }

        return nodes;
    }

}
