package brix.web.nodepage.markup;


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
