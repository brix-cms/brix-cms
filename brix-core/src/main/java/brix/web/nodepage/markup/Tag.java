package brix.web.nodepage.markup;

import java.util.Map;

public interface Tag extends Item
{
	public String getName();

	// The result does not have to be modifiable
	public Map<String, String> getAttributeMap();

	public enum Type
	{
		OPEN, CLOSE, OPEN_CLOSE
	};

	public Type getType();
}
