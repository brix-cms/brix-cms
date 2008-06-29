package brix.web.picker.node;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.jcr.wrapper.BrixNode;
import brix.web.generic.IGenericComponent;
import brix.web.tree.JcrTreeNode;
import brix.web.tree.NodeFilter;

public class NodePickerModalWindow extends ModalWindow implements IGenericComponent<BrixNode>
{
    private final JcrTreeNode rootNode;
    private final NodeFilter enabledFilter;
    private final NodeFilter visibilityFilter;

    public NodePickerModalWindow(String id, IModel<BrixNode> model, JcrTreeNode rootNode, NodeFilter visibilityFilter, NodeFilter enabledFilter)
    {
        super(id, model);
        
        this.rootNode = rootNode; 
        this.enabledFilter = enabledFilter;
        this.visibilityFilter = visibilityFilter;
        
        init();     
    }
    
    public NodePickerModalWindow(String id, JcrTreeNode rootNode, NodeFilter visibilityFilter, NodeFilter enabledFilter)
    {
        super(id);
        
        this.rootNode = rootNode; 
        this.enabledFilter = enabledFilter;
        this.visibilityFilter = visibilityFilter;
        
        init();     
    }
    
    private void init()
    {
    	setWidthUnit("em");
        setInitialWidth(64);
        setUseInitialHeight(false);
        setResizable(false);
        setTitle(new ResourceModel("node-picker-title"));
    }
    
    private void initContent()
    {
        setContent(new NodePickerWithButtons(getContentId(), getModel(), rootNode, visibilityFilter, enabledFilter)
        {
            @Override
            protected void onCancel(AjaxRequestTarget target)
            {
                super.onCancel(target);
                NodePickerModalWindow.this.onCancel(target);
            }

            @Override
            protected void onOk(AjaxRequestTarget target)
            {
                super.onOk(target);
                NodePickerModalWindow.this.onOk(target);
            }
            
            @Override
            public boolean isDisplayFiles()
            {
                return NodePickerModalWindow.this.isDisplayFiles();
            }
        });
    }

    public boolean isDisplayFiles()
    {
        return true;
    }
    
    @Override
    public void show(AjaxRequestTarget target)
    {
        if (isShown() == false)
        {
            initContent();
        }
        super.show(target);
    }

    protected void onCancel(AjaxRequestTarget target)
    {
        close(target);
    }

    protected void onOk(AjaxRequestTarget target)
    {
        close(target);
    }

	@SuppressWarnings("unchecked")
	public IModel<BrixNode> getModel()
	{
		return (IModel<BrixNode>) getDefaultModel();
	}

	public BrixNode getModelObject()
	{
		return (BrixNode) getDefaultModelObject();
	}

	public void setModel(IModel<BrixNode> model)
	{
		setDefaultModel(model);
	}

	public void setModelObject(BrixNode object)
	{
		setDefaultModelObject(object);
	}

}
