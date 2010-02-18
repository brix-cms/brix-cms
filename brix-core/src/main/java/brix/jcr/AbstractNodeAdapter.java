package brix.jcr;

import brix.jcr.wrapper.BrixNode;
import brix.web.reference.Reference;

/**
 * Base class for building node adapters. Adapters are objects that wrap the
 * node and expose it as a business bean.
 * 
 * @author igor.vaynberg
 * 
 */
public class AbstractNodeAdapter
{
	private final BrixNode node;

	public AbstractNodeAdapter(BrixNode node)
	{
		if (node == null)
		{
			throw new IllegalArgumentException("Argument `node` cannot be null");
		}
		this.node = node;
	}


	protected BrixNode getNode()
	{
		return node;
	}

	protected String getProperty(String name, String defaultValue)
	{
		if (getNode().hasProperty(name))
		{
			return getNode().getProperty(name).getString();
		}
		else
		{
			return defaultValue;
		}
	}

	protected Long getProperty(String name, Long defaultValue)
	{
		if (getNode().hasProperty(name))
		{
			return getNode().getProperty(name).getLong();
		}
		else
		{
			return defaultValue;
		}
	}

	protected Integer getProperty(String name, Integer defaultValue)
	{
		Long val = getProperty(name, (Long)null);
		return (val == null) ? defaultValue : val.intValue();
	}
	
	protected BrixNode getProperty(String name, BrixNode defaultValue)
	{
		if (getNode().hasProperty(name)) {
			return (BrixNode)getNode().getProperty(name).getNode();
		} else {
			return defaultValue;
		}
	}
	
	protected void setProperty(String name, BrixNode value) {
		if (value == null)
		{
			if (getNode().hasProperty(name))
			{
				getNode().getProperty(name).remove();
			}
		}
		else
		{
			getNode().setProperty(name, value);
		}
	}

	protected Reference getProperty(String name, Reference defaultValue)
	{
		return Reference.load(getNode(), name);
	}

	protected void setProperty(String name, String value)
	{
		if (value == null)
		{
			if (getNode().hasProperty(name))
			{
				getNode().getProperty(name).remove();
			}
		}
		else
		{
			getNode().setProperty(name, value);
		}
	}

	protected void setProperty(String name, Long value)
	{
		if (value == null)
		{
			if (getNode().hasProperty(name))
			{
				getNode().getProperty(name).remove();
			}
		}
		else
		{
			getNode().setProperty(name, value);
		}
	}

	protected void setProperty(String name, Integer value)
	{
		setProperty(name, (value == null) ? ((Long)null) : value.longValue());
	}

	protected void setProperty(String name, Reference value)
	{
		value.save(getNode(), name);
	}

}
