package brix.web.picker.node;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.web.generic.BrixGenericPanel;
import brix.web.tree.JcrTreeNode;
import brix.web.tree.NodeFilter;

public class NodePickerWithButtons extends BrixGenericPanel<BrixNode>
{

    public NodePickerWithButtons(String id, JcrTreeNode rootNode, NodeFilter visibleFilter, NodeFilter enabledFilter)
    {
        super(id);
        init(rootNode, visibleFilter, enabledFilter);
    }

    public NodePickerWithButtons(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibleFilter, NodeFilter enabledFilter)
    {
        super(id, model);
        init(rootNode, visibleFilter, enabledFilter);
    }

    private IModel<BrixNode> nodeModel;

    public boolean isDisplayFiles()
    {
        return true;
    }
    
    private void init(JcrTreeNode rootNode, NodeFilter visibleFilter, NodeFilter enabledFilter)
    {
    	nodeModel = new BrixNodeModel(getModel().getObject());
    	
        add(new NodePicker("picker", this.nodeModel, rootNode, visibleFilter, enabledFilter));

        add(new AjaxLink<Void>("ok")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onOk(target);
            }
        });

        add(new AjaxLink<Void>("cancel")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onCancel(target);
            }
        });
    }

    protected IModel<BrixNode> getNodeModel()
    {
        return nodeModel;
    }

    protected void onCancel(AjaxRequestTarget target)
    {

    }

    protected void onOk(AjaxRequestTarget target)
    {
        setModelObject(getNodeModel().getObject());
    }
}
