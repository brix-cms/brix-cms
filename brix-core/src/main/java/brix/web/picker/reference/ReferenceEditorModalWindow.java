package brix.web.picker.reference;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import brix.web.generic.IGenericComponent;
import brix.web.reference.Reference;

public class ReferenceEditorModalWindow extends ModalWindow implements IGenericComponent<Reference>
{

	private final ReferenceEditorConfiguration configuration;

	public ReferenceEditorModalWindow(String id, IModel<Reference> model, ReferenceEditorConfiguration configuration)
	{
		super(id);
		setModel(model);

		this.configuration = configuration;

		setWidthUnit("em");
		setInitialWidth(64);
		setUseInitialHeight(false);
		setResizable(false);
		setTitle(new ResourceModel("title"));
	}

	private void initContent()
	{
		setContent(new ReferenceEditorWithButtons(getContentId(), getModel(), configuration)
		{
			@Override
			protected void onCancel(AjaxRequestTarget target)
			{
				super.onCancel(target);
				ReferenceEditorModalWindow.this.onCancel(target);
			}

			@Override
			protected void onOk(AjaxRequestTarget target)
			{
				super.onOk(target);
				ReferenceEditorModalWindow.this.onOk(target);
			}
		});
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
	public IModel<Reference> getModel()
	{
		return (IModel<Reference>) getDefaultModel();
	}

	public Reference getModelObject()
	{
		return (Reference) getDefaultModelObject();
	}

	public void setModel(IModel<Reference> model)
	{
		setDefaultModel(model);
	}

	public void setModelObject(Reference object)
	{
		setDefaultModelObject(object);
	}

}
