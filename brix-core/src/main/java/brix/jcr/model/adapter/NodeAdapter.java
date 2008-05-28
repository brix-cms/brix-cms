package brix.jcr.model.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.model.IModel;

import brix.jcr.api.JcrNode;
import brix.jcr.wrapper.BrixNode;

public abstract class NodeAdapter implements IModel
{

    public static NodeAdapter forNode(final IModel<BrixNode> nodeModel)
    {
        if (nodeModel == null)
        {
            throw new IllegalArgumentException("Parameter 'nodeModel' may not be null.");
        }
        return new NodeAdapter()
        {
            @Override
            JcrNode getNode()
            {
                return nodeModel.getObject();
            }

            @Override
            public void applyValues()
            {
                applyValuesInternal();
            }

            @Override
            public void detach()
            {
                nodeModel.detach();
                super.detach();
            }
        };
    }

    abstract JcrNode getNode();

    public Object getObject()
    {
        return getNode();
    }

    public void setObject(Object object)
    {
        throw new UnsupportedOperationException();
    }

    private List<PropertyAdapter> propertyAdapters = null;

    private PropertyAdapter getPropertyAdapter(String propertyName)
    {
        if (propertyAdapters != null)
        {
            for (PropertyAdapter adapter : propertyAdapters)
            {
                if (adapter.getName().equals(propertyName))
                {
                    return adapter;
                }
            }
        }
        return null;
    }

    private PropertyAdapter getPropertyAdapter(String propertyName,
            Class< ? extends PropertyAdapter> requiredClass)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName);
        if (adapter != null && !requiredClass.isInstance(adapter))
        {
            throw new IllegalStateException("Adapter for property '" + propertyName +
                    "' already exists but with different class.");
        }
        return adapter;
    }

    private void addPropertyAdapter(PropertyAdapter adapter)
    {
        if (propertyAdapters == null)
        {
            propertyAdapters = new ArrayList<PropertyAdapter>();
        }
        propertyAdapters.add(adapter);
    }

    public IModel forBooleanProperty(String propertyName, Boolean defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName, BooleanProperyAdapter.class);
        if (adapter == null)
        {
            adapter = new BooleanProperyAdapter(propertyName, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forBooleanProperty(String propertyName)
    {
        return forBooleanProperty(propertyName, null);
    }

    public IModel forFileContent(String defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(ContentPropertyAdapter.PROPERTY_NAME,
                ContentPropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new ContentPropertyAdapter(defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forFileContent()
    {
        return forFileContent(null);
    }

    public IModel forDateProperty(String propertyName, Date defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName, DatePropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new DatePropertyAdapter(propertyName, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forDateProperty(String propertyName)
    {
        return forDateProperty(propertyName, null);
    }

    public IModel forDelegate(String name, IModel delegate, Object defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(name, DatePropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new DelegatePropertyAdapter(name, delegate, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forDelegate(String name, IModel delegate)
    {
        return forDelegate(name, delegate, null);
    }

    public IModel forDoubleProperty(String propertyName, Double defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName, DoublePropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new DoublePropertyAdapter(propertyName, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forDoubleProperty(String propertyName)
    {
        return forDateProperty(propertyName, null);
    }

    public IModel forLongProperty(String propertyName, Long defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName, LongPropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new LongPropertyAdapter(propertyName, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forLongProperty(String propertyName)
    {
        return forLongProperty(propertyName, null);
    }

    public IModel forNodePathProperty(String propertyName, String defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName, NodePathPropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new NodePathPropertyAdapter(propertyName, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forNodePathProperty(String propertyName)
    {
        return forNodePathProperty(propertyName, null);
    }

    public IModel forNodeProperty(String propertyName, JcrNode defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName, NodePropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new NodePropertyAdapter(propertyName, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forNodeProperty(String propertyName)
    {
        return forNodeProperty(propertyName, null);
    }

    public IModel forStringProperty(String propertyName, String defaultValue)
    {
        PropertyAdapter adapter = getPropertyAdapter(propertyName, StringPropertyAdapter.class);
        if (adapter == null)
        {
            adapter = new StringPropertyAdapter(propertyName, defaultValue, this);
            addPropertyAdapter(adapter);
        }
        return adapter;
    }

    public IModel forStringProperty(String propertyName)
    {
        return forStringProperty(propertyName, null);
    }

    private List<ChildNodeAdapter> childAdapters = null;

    private ChildNodeAdapter getChildNodeAdapter(String name)
    {
        if (childAdapters != null)
        {
            for (ChildNodeAdapter model : childAdapters)
            {
                if (model.getName().equals(name))
                {
                    return model;
                }
            }
        }
        return null;
    }

    private void addChildNodeAdapter(ChildNodeAdapter adapter)
    {
        if (childAdapters == null)
        {
            childAdapters = new ArrayList<ChildNodeAdapter>();
        }
        childAdapters.add(adapter);
    }

    public NodeAdapter forChildNode(String nodeName, String newNodeType,
            String... newNodeMixinTypes)
    {
        ChildNodeAdapter adapter = getChildNodeAdapter(nodeName);

        if (adapter == null)
        {
            adapter = new ChildNodeAdapter(nodeName, newNodeType, newNodeMixinTypes)
            {
                @Override
                NodeAdapter getParent()
                {
                    return this;
                }
            };
            addChildNodeAdapter(adapter);
        }
        return adapter;
    }

    public NodeAdapter forChildNode(String nodeName)
    {
        return forChildNode(nodeName, null, (String[])null);
    }

    public void applyValues()
    {
        throw new UnsupportedOperationException(
                "You must call 'save' on the top-level node adapter.");
    }

    void applyValuesInternal()
    {
        if (remove == true)
        {
            getNode().remove();
        }
        else
        {
            if (propertyAdapters != null)
            {
                for (PropertyAdapter adapter : propertyAdapters)
                {
                    adapter.save();
                }
            }
            if (childAdapters != null)
            {
                for (ChildNodeAdapter adapter : childAdapters)
                {
                    JcrNode node = getNode();
                    if (node.hasNode(adapter.getName()) == false)
                    {
                        adapter.create();
                    }
                    adapter.applyValuesInternal();
                }
            }
        }
    }

    private boolean remove = false;

    public void remove()
    {
        this.remove = true;
    }

    public void detach()
    {
        if (propertyAdapters != null)
        {
            for (PropertyAdapter adapter : propertyAdapters)
            {
                adapter.detach();
            }
        }
        if (childAdapters != null)
        {
            for (ChildNodeAdapter adapter : childAdapters)
            {
                adapter.detach();
            }
        }
    }

}
