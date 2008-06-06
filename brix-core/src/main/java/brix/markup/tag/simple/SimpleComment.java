package brix.markup.tag.simple;

import brix.markup.tag.Comment;

/**
 * Simple implementation of the {@link Comment} interface.
 * 
 * @author Matej Knopp
 */
public class SimpleComment implements Comment
{
	private final String text;
	
	public SimpleComment(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}

}
