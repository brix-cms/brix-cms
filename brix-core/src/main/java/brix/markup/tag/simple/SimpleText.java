package brix.markup.tag.simple;

import brix.markup.tag.Text;

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
