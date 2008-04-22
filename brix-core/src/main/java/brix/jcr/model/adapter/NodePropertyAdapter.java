package brix.jcr.model.adapter;

import brix.jcr.api.JcrNode;
import brix.jcr.api.JcrProperty;

class NodePropertyAdapter extends BasePropertyAdapter
{

    public NodePropertyAdapter(String propertyName, JcrNode defaultValue, NodeAdapter nodeAdapter)
    {
        super(propertyName, defaultValue, nodeAdapter);
    }

    @Override
    protected Object extractValue(JcrProperty property)
    {
        return property.getNode();
    }

    @Override
    protected void setProperty(JcrNode node, String propertyName, Object value)
    {
        node.setProperty(propertyName, (JcrNode)value);
    }

}
