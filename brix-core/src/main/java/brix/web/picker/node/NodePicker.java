package brix.web.picker.node;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import brix.jcr.api.JcrNode;
import brix.plugin.site.SitePlugin;

import brix.web.picker.node.NodePickerTreeModel.NodePickerTreeNode;

import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;
import com.inmethod.grid.column.tree.PropertyTreeColumn;
import com.inmethod.grid.treegrid.TreeGrid;

public class NodePicker extends Panel
{

    public NodePicker(String id, IModel<JcrNode> model, String workspaceName, NodeFilter filter)
    {
        super(id, model);

        this.filter = filter;
        this.workspaceName = workspaceName;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!hasBeenRendered())
        {
            this.treeModel = newTreeModel(workspaceName);
            initComponents();
            selectInitial();
        }
        super.onBeforeRender();
    }

    protected void selectInitial()
    {
        JcrNode node = (JcrNode)getModelObject();
        if (node != null)
        {
            NodePickerTreeNode treeNode = treeModel.treeNodeFor(node);
            grid.selectItem(new Model(treeNode), true);
            
            NodePickerTreeNode parent = (NodePickerTreeNode)treeNode.getParent();
            
            while (parent != null)
            {
                grid.getTreeState().expandNode(parent);
                parent = (NodePickerTreeNode)parent.getParent();
            }
        }
    }

    protected void initComponents()
    {
        grid = new TreeGrid("grid", treeModel, newGridColumns())
        {
            @Override
            protected void onRowClicked(AjaxRequestTarget target, IModel rowModel)
            {
                JcrNode node = ((NodePickerTreeNode)rowModel.getObject()).getNode();
                if (filter == null || filter.isNodeAllowed(node))
                {
                    if (isItemSelected(rowModel) == false)
                    {
                        selectItem(rowModel, true);
                        onNodeSelected(node);
                    }
                    else
                    {
                        selectItem(rowModel, false);
                        onNodeSelected(null);
                    }
                    update();
                }
            }

            @Override
            protected void onRowPopulated(WebMarkupContainer rowComponent)
            {
                super.onRowPopulated(rowComponent);
                rowComponent.add(new AbstractBehavior()
                {
                    @Override
                    public void onComponentTag(Component component, ComponentTag tag)
                    {
                        JcrNode node = ((NodePickerTreeNode)component.getModelObject()).getNode();
                        if (filter != null && filter.isNodeAllowed(node) == false)
                        {
                            tag.put("class", "disabled");
                        }
                    }
                });
            }
        };
        grid.getTree().setRootLess(true);
        grid.setContentHeight(14, SizeUnit.EM);

        add(grid);
    };

    protected void onNodeSelected(JcrNode node)
    {
        setModelObject(node);
    }

    protected List<IGridColumn> newGridColumns()
    {
        IGridColumn columns[] = {
                new PropertyTreeColumn(new ResourceModel("name"), "node.name").setInitialSize(300),
                new PropertyColumn(new ResourceModel("type"), "name") {
                    @Override
                    protected Object getModelObject(IModel rowModel)
                    {
                        NodePickerTreeNode n = (NodePickerTreeNode) rowModel.getObject();
                        return SitePlugin.get().getNodePluginForNode(n.getNode());
                    }
                },
                new DatePropertyColumn(new ResourceModel("lastModified"), "node.lastModified"),
                new PropertyColumn(new ResourceModel("lastModifiedBy"), "node.lastModifiedBy") };
        return Arrays.asList(columns);
    };

    protected NodePickerTreeModel newTreeModel(final String workspaceName)
    {
        return new NodePickerTreeModel(workspaceName)
        {            
            @Override
            protected boolean displayFoldersOnly()
            {
                return !isDisplayFiles();
            }
        };
    }


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

    public void setDisplayFiles(boolean displayFiles)
    {
        this.displayFiles = displayFiles;
    }

    public boolean isDisplayFiles()
    {
        return displayFiles;
    }

    protected TreeGrid getGrid()
    {
        return grid;
    }

    private boolean displayFiles = true;
    private final String workspaceName;
    private final NodeFilter filter;
    private NodePickerTreeModel treeModel;
    private TreeGrid grid;
}
