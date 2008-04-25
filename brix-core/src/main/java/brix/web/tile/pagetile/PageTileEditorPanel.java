package brix.web.tile.pagetile;

import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;
import brix.plugin.site.node.tilepage.TilePageNodePlugin;
import brix.plugin.site.node.tilepage.admin.TileEditorPanel;
import brix.web.picker.node.NodeFilter;
import brix.web.picker.node.NodePickerPanel;
import brix.web.picker.node.NodeTypeFilter;

public class PageTileEditorPanel extends TileEditorPanel
{

    public PageTileEditorPanel(String id, IModel<JcrNode> tileContainerNode)
    {
        super(id, tileContainerNode);

        NodeFilter filter = new NodeTypeFilter(TilePageNodePlugin.TYPE); 
        NodePickerPanel picker = new NodePickerPanel("nodePicker", targetNodeModel, tileContainerNode.getObject().getSession().getWorkspace().getName(), filter);
        picker.setRequired(true);
        add(picker);
    }

    private IModel<JcrNode> targetNodeModel = new BrixNodeModel(null);

    @Override
    public void load(JcrNode node)
    {
        if (node.hasProperty("pageNode"))
        {
            JcrNode pageNode = node.getProperty("pageNode").getNode();
            targetNodeModel.setObject(pageNode);
        }
    }

    @Override
    public void save(JcrNode node)
    {        
        node.setProperty("pageNode", targetNodeModel.getObject());
    }

}
