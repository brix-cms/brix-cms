package brix.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class ModelBuffer implements Serializable
{

    public ModelBuffer()
    {
        this(null);
    }

    public ModelBuffer(Object target)
    {
        this.target = target;
    }

    private final Object target;


    public IModel forModel(IModel delegate)
    {
        if (models == null)
        {
            models = new ArrayList<Model>();
        }
        Model model = new Model(delegate);
        models.add(model);
        return model;
    }

    private Map<String, IModel> propertyMap;

    public IModel forProperty(String propertyName)
    {
        if (target == null)
        {
            throw new IllegalStateException(
                    "The 'forProperty' call requires the target object to be set.");
        }
        if (propertyMap != null && propertyMap.containsKey(propertyName))
        {
            return propertyMap.get(propertyName);
        }
        IModel model = forModel(new PropertyModel(target, propertyName));
        if (propertyMap == null)
        {
            propertyMap = new HashMap<String, IModel>();
        }
        propertyMap.put(propertyName, model);
        return model;
    }

    private List<Model> models = null;

    private static class Model implements IModel
    {
        private final IModel delegate;
        private Object value;
        private boolean valueSet = false;

        public Model(IModel delegate)
        {
            this.delegate = delegate;
        }

        public Object getObject()
        {
            if (valueSet)
            {
                return value;
            }
            else
            {
                return delegate.getObject();
            }
        }

        public void setObject(Object object)
        {
            valueSet = true;
            value = object;
        }

        public void detach()
        {
            delegate.detach();
            if (value instanceof IDetachable)
            {
                ((IDetachable)value).detach();
            }
        }

        public void apply()
        {
            if (valueSet)
            {
                delegate.setObject(value);
                valueSet = false;
            }
        }

    };

    public void apply()
    {
        if (models != null)
        {
            for (Model model : models)
            {
                model.apply();
            }
        }
    }
}
