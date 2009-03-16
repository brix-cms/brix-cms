package brix.jcr.base.wrapper;

import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;
import java.io.InputStream;
import java.util.Calendar;

class PropertyWrapper extends ItemWrapper implements Property
{

    private PropertyWrapper(Property delegate, SessionWrapper session)
    {
        super(delegate, session);
    }

    public static PropertyWrapper wrap(Property delegate, SessionWrapper session)
    {
        if (delegate == null)
        {
            return null;
        }
        else
        {
            return new PropertyWrapper(delegate, session);
        }
    }

    @Override
    public Property getDelegate()
    {
        return (Property)super.getDelegate();
    }

    public boolean getBoolean() throws RepositoryException
    {
        return getDelegate().getBoolean();
    }

    public Calendar getDate() throws RepositoryException
    {
        return getDelegate().getDate();
    }

    public PropertyDefinition getDefinition() throws RepositoryException
    {
        return getDelegate().getDefinition();
    }

    public double getDouble() throws RepositoryException
    {
        return getDelegate().getDouble();
    }

    public long getLength() throws RepositoryException
    {
        return getDelegate().getLength();
    }

    public long[] getLengths() throws RepositoryException
    {
        return getDelegate().getLengths();
    }

    public long getLong() throws RepositoryException
    {
        return getDelegate().getLong();
    }

    public Node getNode() throws RepositoryException
    {
        return NodeWrapper.wrap(getDelegate().getNode(), getSessionWrapper());
    }

    public InputStream getStream() throws RepositoryException
    {
        return getDelegate().getStream();
    }

    public String getString() throws RepositoryException
    {
        return getDelegate().getString();
    }

    public int getType() throws RepositoryException
    {
        return getDelegate().getType();
    }

    public Value getValue() throws RepositoryException
    {
        return getDelegate().getValue();
    }

    public Value[] getValues() throws RepositoryException
    {
        return getDelegate().getValues();
    }

    private void beforeValueSet(Object value) throws RepositoryException
    {
    	if (value == null)
    	{
    		getActionHandler().beforePropertyRemove(getParent(), getName());
    	}
    	else
    	{
    		getActionHandler().beforePropertySet(getParent(), getName());
    	}
    }
    
    private void afterValueSet(Object value) throws RepositoryException 
    {
    	if (value == null)
    	{
    		getActionHandler().afterPropertyRemove(getParent(), getName());
    	}
    	else
    	{
    		getActionHandler().afterPropertySet(this);
    	}
    }
    
    public void setValue(Value value) throws RepositoryException
    {
    	beforeValueSet(value);
        getDelegate().setValue(value);
        afterValueSet(value);
    }

    public void setValue(Value[] values) throws RepositoryException
    {
    	beforeValueSet(values);
        getDelegate().setValue(values);
        afterValueSet(values);
    }

    public void setValue(String value) throws RepositoryException
    {
    	beforeValueSet(value);
        getDelegate().setValue(value);
        afterValueSet(value);
    }

    public void setValue(String[] values) throws RepositoryException
    {
    	beforeValueSet(values);
        getDelegate().setValue(values);
        afterValueSet(values);
    }

    public void setValue(InputStream value) throws RepositoryException
    {
    	beforeValueSet(value);
        getDelegate().setValue(value);
        afterValueSet(value);
    }

    public void setValue(long value) throws RepositoryException
    {
    	beforeValueSet(value);
        getDelegate().setValue(value);
        afterValueSet(value);
    }

    public void setValue(double value) throws RepositoryException
    {
    	beforeValueSet(value);
        getDelegate().setValue(value);
        afterValueSet(value);
    }

    public void setValue(Calendar value) throws RepositoryException
    {
    	beforeValueSet(value);
        getDelegate().setValue(value);
        afterValueSet(value);
    }

    public void setValue(boolean value) throws RepositoryException
    {
    	beforeValueSet(value);
        getDelegate().setValue(value);
        afterValueSet(value);
    }

    public void setValue(Node value) throws RepositoryException
    {        
    	beforeValueSet(value);
        getDelegate().setValue(unwrap(value));
        afterValueSet(value);
    }

    public void accept(ItemVisitor visitor) throws RepositoryException
    {
    	visitor.visit(this);
    }
    
    public boolean isNode()
    {    	
    	return false;
    }

    private String name = null;
    
    @Override
    public String getName() throws RepositoryException
    {
    	if (name == null)
    	{
    		name = super.getName();
    	}
    	return name;
    }
    
    
    private Node parent = null;
    
    @Override
    public Node getParent() throws RepositoryException
    {
    	if (parent == null)
    	{
    		parent = super.getParent();
    	}
    	return parent;
    }
    
    @Override
    public void remove() throws RepositoryException
    {
    	Node parent = getParent();
    	String name = getName();
    	getActionHandler().beforePropertyRemove(parent, name); 
    	super.remove();
    	getActionHandler().afterPropertyRemove(parent, name); 
    }
}
