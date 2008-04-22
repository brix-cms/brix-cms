package brix.web.picker.node;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;

public class NodePickerWithButtons extends Panel
{

    public NodePickerWithButtons(String id, String workspaceName, NodeFilter nodeFilter)
    {
        super(id);
        init(workspaceName, nodeFilter);
    }

    public NodePickerWithButtons(String id, IModel model, String workspaceName, NodeFilter nodeFilter)
    {
        super(id, model);
        init(workspaceName, nodeFilter);
    }

    private IModel<JcrNode> nodeModel;
    
    private void init(String workspaceName, NodeFilter nodeFilter) {
        JcrNode initial = (JcrNode)getModelObject();
        nodeModel = new BrixNodeModel(initial);
        
        add(new NodePicker("picker", this.nodeModel, workspaceName, nodeFilter));
        
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
    
    protected IModel<JcrNode> getNodeModel() {
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
