package brix.web.util;

import org.apache.wicket.model.IModel;

public abstract class AbstractModel<T> implements IModel<T>
{
    public void detach()
    {
    }
    
    public void setObject(T object)
    {
        
    }
    
    public T getObject()
    {     
        return null;
    }
}
