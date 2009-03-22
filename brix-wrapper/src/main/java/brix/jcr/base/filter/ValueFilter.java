package brix.jcr.base.filter;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

public class ValueFilter
{
	private final ValueFilter previous;

	public ValueFilter(ValueFilter previous)
	{
		this.previous = previous;
	}

	public ValueFilter()
	{
		previous = null;
	}

	public ValueFilter getPrevious()
	{
		return previous;
	}

	public Property setValue(Node node, String name, Value value, Integer type) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().setValue(node, name, value, type);
		}
		else
		{
			if (type != null)
			{
				return node.setProperty(name, value, type);	
			}
			else
			{
				return node.setProperty(name, value);	
			}			
		}
	}

	public Property setValue(Node node, String name, Value[] values, Integer type) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().setValue(node, name, values, type);
		}
		else
		{
			if (type != null)
			{
				return node.setProperty(name, values, type);	
			}			
			else
			{
				return node.setProperty(name, values);
			}
		}
	}

	public Value getValue(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getValue(property);
		}
		else
		{
			return property.getValue();
		}
	}
	
	public Value[] getValues(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getValues(property);
		}
		else
		{
			return property.getValues();
		}
	}
	
	public String getString(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getString(property);
		}
		else
		{
			return property.getString();
		}
	}
	
	public InputStream getStream(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getStream(property);
		}
		else
		{
			return property.getStream();
		}
	}
	
	public long getLong(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getLong(property);
		}
		else
		{
			return property.getLong();
		}
	}
	
	public double getDouble(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getDouble(property);
		}
		else
		{
			return property.getDouble();
		}
	}
	
	public Calendar getDate(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getDate(property);
		}
		else
		{
			return property.getDate();
		}
	}
	
	public boolean getBoolean(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getBoolean(property);
		}
		else
		{
			return property.getBoolean();
		}
	}
	
	public Node getNode(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getNode(property);
		}
		else
		{
			return property.getNode();
		}
	}
	
	public long getLength(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getLength(property);
		}
		else
		{
			return property.getLength();
		}
	}
	
	public long[] getLengths(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getLengths(property);
		}
		else
		{
			return property.getLengths();
		}
	}
	
	public int getType(Property property) throws RepositoryException
	{
		if (getPrevious() != null)
		{
			return getPrevious().getType(property);
		}
		else
		{
			return property.getType();
		}
	}
}
