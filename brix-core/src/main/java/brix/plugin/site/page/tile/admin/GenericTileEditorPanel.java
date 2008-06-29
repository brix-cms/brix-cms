package brix.plugin.site.page.tile.admin;

import org.apache.wicket.model.IModel;

import brix.web.generic.IGenericComponent;

public abstract class GenericTileEditorPanel<T> extends TileEditorPanel implements IGenericComponent<T>
{

	public GenericTileEditorPanel(String id)
	{
		super(id);
	}

	public GenericTileEditorPanel(String id, IModel<?> model)
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
