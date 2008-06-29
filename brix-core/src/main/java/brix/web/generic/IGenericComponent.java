package brix.web.generic;

import org.apache.wicket.model.IModel;

public interface IGenericComponent <T>
{	
	public IModel<T> getModel();

	public void setModel(IModel<T> model);

	public T getModelObject();

	public void setModelObject(T object);
	
}
