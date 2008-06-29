package brix.web.generic;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class BrixGenericPanel<T> extends Panel implements IGenericComponent<T>
{
	public BrixGenericPanel(String id)
	{
		super(id);
	}

	public BrixGenericPanel(String id, IModel<T> model)
	{
		super(id, model);
	}

	@SuppressWarnings("unchecked")
	public final IModel<T> getModel()
	{
		return (IModel<T>) getDefaultModel();
	}

	@SuppressWarnings("unchecked")
	public final T getModelObject()
	{
		return (T) getDefaultModelObject();
	}

	public final void setModel(IModel<T> model)
	{
		setDefaultModel(model);
	}

	public final void setModelObject(T object)
	{
		setDefaultModelObject(object);
	}

}
