package brix.markup.tag.simple;

import java.util.Map;

import brix.markup.tag.Tag;

/**
 * Simple implementation of the {@link Tag} interface.
 * 
 * @author Matej Knopp
 */
public class SimpleTag implements Tag
{
	private final Map<String, String> attributeMap;	
	private final String name;	
	private final Type type;
	
	public SimpleTag(String name, Type type, Map<String, String> attributeMap)
	{
		this.name = name;
		this.type = type;
		this.attributeMap = attributeMap;
	}

	public Map<String, String> getAttributeMap()
	{	
		return attributeMap;
	}

	public String getName()
	{
		return name;
	}

	public Type getType()
	{
		return type;
	}

}
