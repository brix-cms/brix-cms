package brix.jcr.model.adapter;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;

class LongPropertyAdapter extends BasePropertyAdapter
{

    public LongPropertyAdapter(String propertyName, Long defaultValue, NodeAdapter nodeAdapter)
    {
        super(propertyName, defaultValue, nodeAdapter);
    }

    @Override
    protected Object extractValue(JcrProperty property)
    {
        return property.getLong();
    }

    @Override
    protected void setProperty(JcrNode node, String propertyName, Object value)
    {
        node.setProperty(propertyName, (Long)value);
    }

}
