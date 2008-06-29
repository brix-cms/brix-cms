package brix.rmiserver.web.admin;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;

public class GenericFragment<T> extends Fragment 
{

	public GenericFragment(String id, String markupId, MarkupContainer markupProvider)
	{
		super(id, markupId, markupProvider);
	}

	public GenericFragment(String id, String markupId, MarkupContainer markupProvider, IModel<T> model)
	{
		super(id, markupId, markupProvider, model);
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
