package brix.rmiserver.web.admin;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class GenericPanel<T> extends Panel 
{
	public GenericPanel(String id)
	{
		super(id);
	}

	public GenericPanel(String id, IModel<T> model)
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
