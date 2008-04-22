package brix.web.util;

import org.apache.wicket.model.IModel;

public abstract class ChainedModel implements IModel
{

    private final IModel chained;

    public ChainedModel(IModel model)
    {
        super();
        this.chained = model;
    }

    protected abstract Object getObject(IModel chained);

    protected abstract void setObject(Object object, IModel chained);

    public final Object getObject()
    {
        return getObject(chained);
    }

    public void setObject(Object object)
    {
        setObject(object, chained);
    }

    public void detach()
    {
        chained.detach();
    }
}
