package brix.web.util;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public abstract class ChainedLdmModel extends LoadableDetachableModel
{

    private final IModel chained;

    public ChainedLdmModel(IModel model)
    {
        super();
        this.chained = model;
    }

    @Override
    protected Object load()
    {
        return load(chained);
    }

    @Override
    protected void onDetach()
    {
        chained.detach();
        super.onDetach();
    }

    protected abstract Object load(IModel chained);

}
