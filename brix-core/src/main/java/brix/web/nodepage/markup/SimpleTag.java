package brix.web.nodepage.markup;

import java.util.Map;


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
