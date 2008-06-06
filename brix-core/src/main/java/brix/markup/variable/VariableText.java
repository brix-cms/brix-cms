package brix.markup.variable;

import java.util.Arrays;
import java.util.Collection;

import brix.BrixNodeModel;
import brix.jcr.wrapper.BrixNode;
import brix.markup.tag.Text;

public class VariableText implements Text, VariableKeyProvider
{
	private final BrixNodeModel pageNodeModel;
	private final String key;

	public VariableText(BrixNode pageNode, String key)
	{
		this.pageNodeModel = new BrixNodeModel(pageNode);
		this.key = key;
		this.pageNodeModel.detach();
	}
	
	public String getText()
	{
		BrixNode node = new BrixNodeModel(pageNodeModel).getObject();
		if (node instanceof VariableValueProvider)
		{
			return ((VariableValueProvider) node).getVariableValue(key);
		}
		else
		{
			return "Couldn't resolve variable '" + key + "'";
		}
	}

	public Collection<String> getVariableKeys()
	{
		return Arrays.asList(new String[] { key } );
	}
}
