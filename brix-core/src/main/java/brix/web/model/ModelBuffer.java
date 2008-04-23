package brix.web.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import brix.BrixNodeModel;
import brix.jcr.api.JcrNode;

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


    public IModel forModel(IModel delegate, boolean isNode)
    {
        if (models == null)
        {
            models = new ArrayList<Model>();
        }
        Model model;
        if (!isNode)
            model = new Model(delegate);
        else
            model = new NodeModel(delegate);
        
        models.add(model);
        return model;
    }

    private Map<String, IModel> propertyMap;

    protected IModel forProperty(String propertyName, boolean isNode)
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
        IModel model = forModel(new PropertyModel(target, propertyName), isNode);
        if (propertyMap == null)
        {
            propertyMap = new HashMap<String, IModel>();
        }
        propertyMap.put(propertyName, model);
        return model;
    }
    
    public IModel forProperty(String propertyName) 
    {
        return forProperty(propertyName, false); 
    }
    
    public IModel forNodeProperty(String propertyName)
    {
        return forProperty(propertyName, true);
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

        protected void apply(IModel delegate, Object value)
        {
            delegate.setObject(value);
        }

        public void apply()
        {
            if (valueSet)
            {
                apply(delegate, value);
                valueSet = false;
            }
        }

    };

    private static class NodeModel extends Model
    {
        public NodeModel(IModel delegate)
        {
            super(delegate);
        }

        @Override
        public Object getObject()
        {
            Object value = super.getObject();
            if (value instanceof IModel)
            {
                return ((IModel)value).getObject();
            }
            else
            {
                return value;
            }
        }

        @Override
        public void setObject(Object object)
        {
            if (object instanceof JcrNode)
            {
                super.setObject(new BrixNodeModel((JcrNode)object));
            }
            else
            {
                super.setObject(object);
            }
        }

        @Override
        protected void apply(IModel delegate, Object value)
        {
            if (value instanceof IModel)
            {
                value = ((IModel)value).getObject();
            }
            super.apply(delegate, value);
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
