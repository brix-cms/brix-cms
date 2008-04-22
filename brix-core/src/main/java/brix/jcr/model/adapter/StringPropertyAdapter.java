package brix.jcr.model.adapter;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;

class StringPropertyAdapter extends BasePropertyAdapter
{

    public StringPropertyAdapter(String propertyName, String defaultValue, NodeAdapter nodeAdapter)
    {
        super(propertyName, defaultValue, nodeAdapter);
    }

    @Override
    protected Object extractValue(JcrProperty property)
    {
        return property.getString();
    }

    @Override
    protected void setProperty(JcrNode node, String propertyName, Object value)
    {
        node.setProperty(propertyName, (String)value);
    }

}
