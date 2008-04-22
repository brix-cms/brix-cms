package brix.jcr.model.adapter;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;

class BooleanProperyAdapter extends BasePropertyAdapter
{

    public BooleanProperyAdapter(String propertyName, Boolean defaultValue, NodeAdapter nodeAdapter)
    {
        super(propertyName, defaultValue, nodeAdapter);
    }

    @Override
    protected Object extractValue(JcrProperty property)
    {
        return property.getBoolean();
    }

    @Override
    protected void setProperty(JcrNode node, String propertyName, Object value)
    {
        node.setProperty(propertyName, (Boolean)value);
    }

}
