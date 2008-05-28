package brix.web.picker.node;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;

public class NodePickerWithButtons extends Panel<BrixNode>
{

    public NodePickerWithButtons(String id, String workspaceName, NodeFilter nodeFilter)
    {
        super(id);
        init(workspaceName, nodeFilter);
    }

    public NodePickerWithButtons(String id, IModel<BrixNode> model, String workspaceName,
            NodeFilter nodeFilter)
    {
        super(id, model);
        init(workspaceName, nodeFilter);
    }

    private IModel<BrixNode> nodeModel;

    public boolean isDisplayFiles()
    {
        return true;
    }
    
    private void init(String workspaceName, NodeFilter nodeFilter)
    {
        BrixNode initial = getModelObject();
        nodeModel = new BrixNodeModel(initial);

        add(new NodePicker("picker", this.nodeModel, workspaceName, nodeFilter)
        {
            @Override
            public boolean isDisplayFiles()
            {
                return NodePickerWithButtons.this.isDisplayFiles();
            }
        });

        add(new AjaxLink("ok")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                onOk(target);
            }
        });

        add(new AjaxLink("cancel")
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
