package brix.plugin.template;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.SitePlugin;
import brix.plugin.template.SelectItemsTreeModel.SelectItemsTreeNode;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.CheckBoxColumn;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.column.tree.PropertyTreeColumn;
import com.inmethod.grid.treegrid.TreeGrid;

public class SelectItemsPanel<T> extends Panel<T>
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
    
    protected List<IGridColumn> newGridColumns()
    {
        IGridColumn columns[] = { new CheckBoxColumn("checkbox"),
            new PropertyTreeColumn(new ResourceModel("name"), "node.name").setInitialSize(300),
            new PropertyColumn(new ResourceModel("type"), "name")
            {
                @Override
                protected Object getModelObject(IModel rowModel)
                {
                    SelectItemsTreeNode n = (SelectItemsTreeNode)rowModel.getObject();
                    return SitePlugin.get().getNodePluginForNode(n.getNode());
                }
            }, new DatePropertyColumn(new ResourceModel("lastModified"), "node.lastModified"),
            new PropertyColumn(new ResourceModel("lastModifiedBy"), "node.lastModifiedBy") };
        return Arrays.asList(columns);
    };

    protected class DatePropertyColumn extends PropertyColumn
    {

        public DatePropertyColumn(IModel headerModel, String propertyExpression)
        {
            super(headerModel, propertyExpression);
        }

        @Override
        protected CharSequence convertToString(Object object)
        {
            if (object instanceof Date)
            {
                Date date = (Date)object;
                return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(
                    date);
            }
            else
            {
                return null;
            }
        }
    };

    private TreeGrid treeGrid;
    
    private void init(String workspaceName)
    {
        treeGrid = new TreeGrid("grid", new SelectItemsTreeModel(workspaceName), newGridColumns());
        treeGrid.setContentHeight(15, SizeUnit.EM);
        treeGrid.setClickRowToSelect(true);
        treeGrid.setAllowSelectMultiple(true);
        treeGrid.getTree().setRootLess(true);

        add(treeGrid);
    }

    public TreeGrid getTreeGrid()
    {
        return treeGrid;
    }
    
    private String getNodePath(JcrNode node)
    {
        String path = node.getPath();
        if (path.startsWith(SitePlugin.get().getSiteRootPath()))
        {
            return SitePlugin.get().pathForNode(node);           
        }
        else
        {
            return path;
        }
    }
    
    protected String getDependenciesMessage(Map<JcrNode, List<JcrNode>> dependencies)
    {
        StringBuilder b = new StringBuilder();
        
        b.append("The following dependencies are not satisfied:\n");
        
        for (Entry<JcrNode, List<JcrNode>> entry : dependencies.entrySet())
        {
            b.append(getNodePath(entry.getKey()));
            b.append(" -> ");
            
            if (entry.getValue().size() == 1)
            {
                b.append(getNodePath(entry.getValue().iterator().next()));
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
                    b.append(getNodePath(node));
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
            SelectItemsTreeNode node = (SelectItemsTreeNode)model.getObject();
            nodes.add(node.getNode());
        }

        return nodes;
    }

}
