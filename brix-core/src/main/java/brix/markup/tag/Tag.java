package brix.markup.tag;

import java.util.Map;

/**
 * Represents an HTML tag.
 * @author Matej Knopp
 * 
 * @see ComponentTag
 */
public interface Tag extends Item
{
	/**
	 * Returns the name of this tag ("a", "p", "br", "input", etc.). 
	 * @return
	 */
	public String getName();

	/**
	 * Returns map of tag attributes. The result map does not
	 * have to be modifiable.
	 */
	public Map<String, String> getAttributeMap();

	/**
	 * Tag type
	 * @author Matej Knopp
	 */
	public enum Type
	{
		OPEN, CLOSE, OPEN_CLOSE
	};

	/**
	 * Returns the type of this tag.
	 * @return
	 */
	public Type getType();
}
