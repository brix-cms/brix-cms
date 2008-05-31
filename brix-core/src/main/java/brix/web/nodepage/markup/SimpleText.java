package brix.web.nodepage.markup;


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
