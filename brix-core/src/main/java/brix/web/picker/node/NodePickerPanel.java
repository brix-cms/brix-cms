package brix.web.picker.node;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;
import brix.plugin.site.SitePlugin;

public class NodePickerPanel extends FormComponentPanel<BrixNode>
{

    public NodePickerPanel(String id, String workspaceName, NodeFilter nodeFilter)
    {
        super(id);
        this.workspaceName = workspaceName;
        this.nodeFilter = nodeFilter;
    }

    public NodePickerPanel(String id, IModel<BrixNode> model, String workspaceName, NodeFilter nodeFilter)
    {
        super(id, model);
        this.workspaceName = workspaceName;
        this.nodeFilter = nodeFilter;
    }
    
    private final String workspaceName;
    private final NodeFilter nodeFilter;

    public String getWorkspaceName()
    {
        return workspaceName;
    }
    
    public NodeFilter getNodeFilter()
    {
        return nodeFilter;
    }
    
    @Override
    protected void onBeforeRender()
    {
        super.onBeforeRender();
        if (!hasBeenRendered())
        {
            init();
        }
    }

    protected static final String MODAL_WINDOW_ID = "modalWindow";

    @Override
    public void updateModel()
    {
        // don't you dare!
    }

    private void init()
    {
        add(newModalWindow(MODAL_WINDOW_ID));
        final Label<?> label = new Label<String>("label", newLabelModel())
        {
        	@Override
        	public boolean isVisible()
        	{
        		return NodePickerPanel.this.getModelObject() != null;
        	}
        };
        setOutputMarkupId(true);
        add(label);

        add(new AjaxLink<Void>("edit")
        {
            @Override
            public void onClick(AjaxRequestTarget target)
            {
                getModalWindow().setModel(NodePickerPanel.this.getModel());
                getModalWindow().setWindowClosedCallback(new ModalWindow.WindowClosedCallback()
                {
                    public void onClose(AjaxRequestTarget target)
                    {
                        target.addComponent(NodePickerPanel.this);
                        NodePickerPanel.this.onClose(target);
                    }
                });
                getModalWindow().show(target);
            }
        });
        
        add(new AjaxLink<Void>("clear") {
        	@Override
        	public void onClick(AjaxRequestTarget target)
        	{
        		NodePickerPanel.this.setModelObject(null);
        		target.addComponent(NodePickerPanel.this);
        	}
        	@Override
        	public boolean isEnabled()
        	{
        		return NodePickerPanel.this.getModelObject() != null;
        	}
        });
    }

    protected void onClose(AjaxRequestTarget target)
    {

    }

    protected NodePickerModalWindow getModalWindow()
    {
        return (NodePickerModalWindow)get(MODAL_WINDOW_ID);
    }

    protected IModel<String> newLabelModel()
    {
        return new Model<String>()
        {
            @Override
            public String getObject()
            {
                IModel<BrixNode> model = NodePickerPanel.this.getModel();
                BrixNode node = (BrixNode)model.getObject();
                return node != null ? SitePlugin.get().pathForNode(node) : "";
            }
        };
    }

    
    protected Component<?> newModalWindow(String id)
    {
        return new NodePickerModalWindow(id, getModel(), getWorkspaceName(), getNodeFilter()) {
            @Override
            public boolean isDisplayFiles()
            {
                return NodePickerPanel.this.isDisplayFiles();
            }
        };
    }

    public boolean isDisplayFiles()
    {
        return true;
    }
    
    
    @Override
    public boolean checkRequired()
    {
        if (isRequired())
        {
            JcrNode node = (JcrNode) getModelObject();
            if (node == null)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isInputNullable()
    {
        return false;
    }
}
