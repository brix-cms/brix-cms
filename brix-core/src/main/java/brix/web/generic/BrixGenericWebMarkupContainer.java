package brix.web.generic;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

public class BrixGenericWebMarkupContainer<T> extends WebMarkupContainer implements IGenericComponent<T>
{

	public BrixGenericWebMarkupContainer(String id)
	{
		super(id);
	}

	public BrixGenericWebMarkupContainer(String id, IModel<T> model)
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
