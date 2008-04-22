/**
 * 
 */
package brix.jcr.model.adapter;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;

abstract class BasePropertyAdapter extends PropertyAdapter
{

    private final NodeAdapter nodeAdapter;
    private final String propertyName;
    private Object value;
    private final Object defaultValue;
    private boolean valueSet = false;

    BasePropertyAdapter(String propertyName, Object defaultValue, NodeAdapter nodeAdapter)
    {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.nodeAdapter = nodeAdapter;
    }

    public void setObject(Object object)
    {
        valueSet = true;
        this.value = object;
    }

    abstract protected Object extractValue(JcrProperty property);

    abstract protected void setProperty(JcrNode node, String propertyName, Object value);

    JcrNode getNode()
    {
        return nodeAdapter.getNode();
    }

    public Object getObject()
    {
        if (valueSet)
        {
            return value;
        }
        else
        {
            JcrNode node = getNode();
            if (node != null && node.hasProperty(propertyName))
            {
                return extractValue(node.getProperty(propertyName));
            }
            else
            {
                return defaultValue;
            }
        }
    }

    void save()
    {
        if (valueSet)
        {
            JcrNode node = getNode();
            if (value == null)
            {
                // this would break primitive types. We handle it here so that we don't need this
                // check
                // in every PropertyAdapter for primitive type
                node.setProperty(propertyName, (String)value);
            }
            else
            {
                setProperty(node, propertyName, value);
            }
            valueSet = false;
        }
    }

    public String getName()
    {
        return propertyName;
    }

    public void detach()
    {
    }
}