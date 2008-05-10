package brix.plugin.template;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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

public class SelectItemsPanel extends Panel<Void>
{
    private final String workspaceName;

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

    public SelectItemsPanel(String id, String workspaceName, final String targetWorkspaceName)
    {
        super(id);
        this.workspaceName = workspaceName;

        treeGrid = new TreeGrid("grid", new SelectItemsTreeModel(workspaceName), newGridColumns());
        treeGrid.setContentHeight(15, SizeUnit.EM);
        treeGrid.setClickRowToSelect(true);
        treeGrid.setAllowSelectMultiple(true);
        treeGrid.getTree().setRootLess(true);

        add(treeGrid);
        
        final Component<String> message = new MultiLineLabel<String>("message", new Model<String>(""));
        message.setOutputMarkupId(true);
        add(message);

        add(new AjaxLink<Void>("restore")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                List<JcrNode> nodes = new ArrayList<JcrNode>();
                for (IModel model : treeGrid.getSelectedItems())
                {
                    SelectItemsTreeNode node = (SelectItemsTreeNode)model.getObject();
                    nodes.add(node.getNodeModel().getObject());
                }
                List<String> result = TemplatePlugin.get().restoreNodes(nodes, targetWorkspaceName);
                
                if (!result.isEmpty())
                {
                    StringBuilder msg = new StringBuilder();
                    msg.append("The following nodes have dependencies outside selected items:\n");
                    for (String s : result)
                    {
                        msg.append(s);
                        msg.append("\n");
                    }
                    message.setModelObject(msg.toString());
                    target.addComponent(message);
                } 
                else
                {
                    findParent(ModalWindow.class).close(target);
                }
            }
        });
    }


}
