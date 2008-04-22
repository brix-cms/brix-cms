package brix.jcr.model.adapter;

import brix.Brix;
import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixFileNode;

class ContentPropertyAdapter extends PropertyAdapter
{

    public ContentPropertyAdapter(String defaultValue, NodeAdapter nodeAdapter)
    {
        this.defaultValue = defaultValue;
        this.nodeAdapter = nodeAdapter;
    }

    private final NodeAdapter nodeAdapter;
    private final String defaultValue;
    private String value = null;
    private boolean valueSet = false;

    static final String PROPERTY_NAME = Brix.NS_PREFIX + "contentPropertyAdapter";

    @Override
    JcrNode getNode()
    {
        return nodeAdapter.getNode();
    }

    @Override
    String getName()
    {
        return PROPERTY_NAME;
    }

    @Override
    void save()
    {
        if (valueSet)
        {
            ((BrixFileNode)getNode()).setData(value);
            valueSet = false;
        }
    }

    public Object getObject()
    {
        JcrNode node = getNode();
        if (node != null)
        {
            String content = ((BrixFileNode)getNode()).getDataAsString();
            if (content != null)
            {
                return content;
            }
        }
        return defaultValue;
    }

    public void setObject(Object object)
    {
        this.value = (String)object;
        valueSet = true;
    }

    public void detach()
    {

    }

}
