package brix.jcr.model.adapter;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;

class DoublePropertyAdapter extends BasePropertyAdapter
{

    public DoublePropertyAdapter(String propertyName, Double defaultValue, NodeAdapter nodeAdapter)
    {
        super(propertyName, defaultValue, nodeAdapter);
    }

    @Override
    protected Object extractValue(JcrProperty property)
    {
        return property.getDouble();
    }

    @Override
    protected void setProperty(JcrNode node, String propertyName, Object value)
    {
        node.setProperty(propertyName, (Double)value);
    }

}
