package brix.jcr.model.adapter;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;

public class DelegatePropertyAdapter extends PropertyAdapter
{

    private final String name;
    private final IModel delegate;
    private final Object defaultValue;
    private final NodeAdapter nodeAdapter;

    public DelegatePropertyAdapter(String name, IModel delegate, Object defaultValue,
            NodeAdapter nodeAdapter)
    {
        this.name = name;
        this.delegate = delegate;
        this.defaultValue = defaultValue;
        this.nodeAdapter = nodeAdapter;
    }

    @Override
    JcrNode getNode()
    {
        return nodeAdapter.getNode();
    }

    @Override
    String getName()
    {
        return name;
    }

    private Object value;
    private boolean valueSet = false;

    @Override
    void save()
    {
        if (valueSet == true)
        {
            delegate.setObject(value);
            valueSet = false;
        }
    }

    public Object getObject()
    {
        if (valueSet)
        {
            return value;
        }
        else
        {
            Object value = delegate.getObject();
            if (value == null)
            {
                return defaultValue;
            }
            else
            {
                return value;
            }
        }
    }

    public void setObject(Object object)
    {
        value = object;
        valueSet = true;
    }

    public void detach()
    {
        delegate.detach();
    }

}
