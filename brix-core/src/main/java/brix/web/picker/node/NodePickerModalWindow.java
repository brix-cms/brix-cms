package brix.web.picker.node;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class NodePickerModalWindow extends ModalWindow
{

    private final String workspaceName;
    private final NodeFilter nodeFilter;

    public NodePickerModalWindow(String id, IModel /* <Reference> */model, String workspaceName,
            NodeFilter nodeFilter)
    {
        super(id);
        setModel(model);

        this.workspaceName = workspaceName;
        this.nodeFilter = nodeFilter;

        setWidthUnit("em");
        setInitialWidth(64);
        setUseInitialHeight(false);
        setResizable(false);
        setTitle(new ResourceModel("title"));
    }

    private void initContent()
    {
        setContent(new NodePickerWithButtons(getContentId(), getModel(), workspaceName, nodeFilter)
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

}
