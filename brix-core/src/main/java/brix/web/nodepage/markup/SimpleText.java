package brix.web.nodepage.markup;

/**
 * Simple implementation of the {@link Text} interface
 * 
 * @author Matej Knopp
 */
public class SimpleText implements Text
{
	private final String text;
	
	public SimpleText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}

}
